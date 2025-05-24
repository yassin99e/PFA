// src/app/components/messaging/chat/chat.component.ts
import { Component, OnInit, OnDestroy, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormControl, Validators } from '@angular/forms';
import { MessagingService, Conversation, Message } from '../../../services/messaging.service';
import { UserService } from '../../../services/user.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css'],
  standalone: false
})
export class ChatComponent implements OnInit, OnDestroy, AfterViewChecked {
  @ViewChild('messageContainer') private messageContainer?: ElementRef;

  conversationId?: number;
  conversation?: Conversation;
  messages: Message[] = [];
  messageContent = new FormControl('', [Validators.required]);
  loading = true;
  error: string | null = null;

  // Store the other participant's name
  otherParticipantName: string = '';

  private newMessageSubscription?: Subscription;

  constructor(
    private route: ActivatedRoute,
    private messagingService: MessagingService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.conversationId = +id;
        this.loadConversation();
      }
    });

    // Subscribe to new messages
    this.newMessageSubscription = this.messagingService.newMessage$.subscribe(
      message => {
        if (message && message.conversationId === this.conversationId) {
          this.messages.push(message);
          this.markAsRead(message);
          this.scrollToBottom();
        }
      }
    );

    // Connect to WebSocket
    const currentUser = this.userService.getCurrentUser();
    if (currentUser) {
      this.messagingService.connect(currentUser.id);
    }
  }

  ngOnDestroy(): void {
    if (this.newMessageSubscription) {
      this.newMessageSubscription.unsubscribe();
    }
    this.messagingService.disconnect();
  }

  ngAfterViewChecked(): void {
    this.scrollToBottom();
  }

  private scrollToBottom(): void {
    try {
      if (this.messageContainer) {
        this.messageContainer.nativeElement.scrollTop =
          this.messageContainer.nativeElement.scrollHeight;
      }
    } catch (err) {}
  }

  protected loadConversation(): void {
    if (!this.conversationId) return;

    this.loading = true;
    this.messagingService.getConversation(this.conversationId).subscribe({
      next: (conversation) => {
        this.conversation = conversation;
        this.messages = conversation.messages;

        // Load the other participant's name
        this.loadOtherParticipantName();

        this.loading = false;

        // Mark all messages as read
        this.markAllAsRead();

        setTimeout(() => this.scrollToBottom(), 100);
      },
      error: (err) => {
        console.error('Error loading conversation:', err);
        this.error = 'Failed to load conversation. Please try again.';
        this.loading = false;
      }
    });
  }

  private loadOtherParticipantName(): void {
    if (!this.conversation) return;

    const currentUser = this.userService.getCurrentUser();
    if (!currentUser) return;

    // Determine which participant is the other user
    const otherUserId =
      currentUser.id === this.conversation.participantOneId ?
        this.conversation.participantTwoId :
        this.conversation.participantOneId;

    // Fetch the real name from the UserService
    this.userService.getUserNameById(otherUserId).subscribe({
      next: (name) => {
        this.otherParticipantName = name;
      },
      error: (err) => {
        console.error('Error loading participant name:', err);
        this.otherParticipantName = `User ${otherUserId}`;
      }
    });
  }

  private markAllAsRead(): void {
    if (!this.conversationId) return;

    const currentUser = this.userService.getCurrentUser();
    if (!currentUser) return;

    // Mark unread messages as read
    const unreadMessages = this.messages.filter(
      msg => msg.receiverId === currentUser.id && !msg.isRead
    );

    if (unreadMessages.length > 0) {
      this.messagingService.markConversationAsRead(this.conversationId).subscribe();

      // Update local state
      unreadMessages.forEach(msg => msg.isRead = true);
    }
  }

  private markAsRead(message: Message): void {
    const currentUser = this.userService.getCurrentUser();
    if (!currentUser || message.receiverId !== currentUser.id || message.isRead) {
      return;
    }

    this.messagingService.markAsRead(message.id!).subscribe({
      next: () => {
        message.isRead = true;
      },
      error: (err) => {
        console.error('Error marking message as read:', err);
      }
    });
  }

  sendMessage(): void {
    if (!this.messageContent.value || !this.conversationId) {
      return;
    }

    const currentUser = this.userService.getCurrentUser();
    if (!currentUser) return;

    const newMessage: Message = {
      conversationId: this.conversationId,
      senderId: currentUser.id,
      content: this.messageContent.value,
      senderRole: currentUser.role
    };

    this.messagingService.sendMessage(newMessage).subscribe({
      next: (sentMessage) => {
        this.messages.push(sentMessage);
        this.messageContent.reset();
        setTimeout(() => this.scrollToBottom(), 100);
      },
      error: (err) => {
        console.error('Error sending message:', err);
        this.error = 'Failed to send message. Please try again.';
      }
    });
  }

  // Updated method to return the loaded name
  getOtherParticipantName(): string {
    return this.otherParticipantName || 'Loading...';
  }

  isOnline(): boolean {
    // Placeholder - in real app, would check with user status service
    return true;
  }

  isSentByCurrentUser(message: Message): boolean {
    const currentUser = this.userService.getCurrentUser();
    return currentUser !== null && message.senderId === currentUser.id;
  }

  formatTime(timestamp?: Date): string {
    if (!timestamp) return '';

    const date = new Date(timestamp);
    const now = new Date();

    // Today
    if (date.toDateString() === now.toDateString()) {
      return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    }

    // Yesterday
    const yesterday = new Date(now);
    yesterday.setDate(now.getDate() - 1);
    if (date.toDateString() === yesterday.toDateString()) {
      return `Yesterday, ${date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}`;
    }

    // This week
    const oneWeekAgo = new Date(now);
    oneWeekAgo.setDate(now.getDate() - 7);
    if (date > oneWeekAgo) {
      return date.toLocaleDateString([], { weekday: 'short' }) +
        ' ' + date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    }

    // Older
    return date.toLocaleDateString([], {
      year: '2-digit', month: 'short', day: 'numeric'
    });
  }
}
