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
  otherParticipantName: string = '';

  private newMessageSubscription?: Subscription;
  private readReceiptSubscription?: Subscription;

  constructor(
    private route: ActivatedRoute,
    private messagingService: MessagingService,
    private userService: UserService
  ) {}

  async ngOnInit(): Promise<void> {
    // Get conversation ID from route
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.conversationId = +id;
        this.loadConversation();
      }
    });

    // Connect to WebSocket
    const currentUser = this.userService.getCurrentUser();
    if (currentUser) {
      try {
        await this.messagingService.connect(currentUser.id);
        console.log('WebSocket connected successfully');
        this.setupMessageSubscriptions();
      } catch (error) {
        console.error('Failed to connect to WebSocket:', error);
        this.setupMessageSubscriptions();
      }
    }
  }

  private setupMessageSubscriptions(): void {
    // Subscribe to new messages
    this.newMessageSubscription = this.messagingService.newMessage$.subscribe(
      message => {
        if (message && message.conversationId === this.conversationId) {
          console.log('✅ Received new message in chat:', message);

          // Normalize the read property
          if (message.read !== undefined) {
            message.isRead = message.read;
          }

          // Check if message already exists to avoid duplicates
          const existingMessage = this.messages.find(m => m.id === message.id);
          if (!existingMessage) {
            this.messages.push(message);
            this.sortMessages();
            this.markAsRead(message);
            setTimeout(() => this.scrollToBottom(), 100);
          }
        }
      }
    );

    // Subscribe to read receipts
    this.readReceiptSubscription = this.messagingService.readReceipt$.subscribe(
      receipt => {
        if (receipt && receipt.messageId) {
          const message = this.messages.find(m => m.id === receipt.messageId);
          if (message) {
            message.isRead = receipt.isRead;
            message.read = receipt.isRead; // Keep both for compatibility
          }
        }
      }
    );
  }

  private sortMessages(): void {
    this.messages.sort((a, b) => {
      const dateA = new Date(a.timestamp || 0);
      const dateB = new Date(b.timestamp || 0);
      return dateA.getTime() - dateB.getTime();
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
      senderRole: currentUser.role,
      timestamp: new Date(),
      isRead: false
    };

    this.messagingService.sendMessage(newMessage).subscribe({
      next: () => {
        this.messageContent.reset();
        setTimeout(() => this.scrollToBottom(), 100);
      },
      error: (err) => {
        console.error('Error sending message:', err);
        this.error = 'Failed to send message. Please try again.';
      }
    });
  }

  ngOnDestroy(): void {
    if (this.newMessageSubscription) {
      this.newMessageSubscription.unsubscribe();
    }
    if (this.readReceiptSubscription) {
      this.readReceiptSubscription.unsubscribe();
    }
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
        this.messages = conversation.messages || [];

        // Normalize read properties for all messages
        this.messages.forEach(msg => {
          if (msg.read !== undefined) {
            msg.isRead = msg.read;
          }
        });

        // Sort messages by timestamp
        this.messages.sort((a, b) => {
          const dateA = new Date(a.timestamp || 0);
          const dateB = new Date(b.timestamp || 0);
          return dateA.getTime() - dateB.getTime();
        });

        this.loadOtherParticipantName();
        this.loading = false;
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

    const otherUserId =
      currentUser.id === this.conversation.participantOneId ?
        this.conversation.participantTwoId :
        this.conversation.participantOneId;

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

    // Mark unread messages as read - check both properties
    const unreadMessages = this.messages.filter(
      msg => msg.receiverId === currentUser.id &&
        (msg.isRead === false || msg.read === false)
    );

    if (unreadMessages.length > 0) {
      this.messagingService.markConversationAsRead(this.conversationId).subscribe();
      // Update local state
      unreadMessages.forEach(msg => {
        msg.isRead = true;
        msg.read = true;
      });
    }
  }

  private markAsRead(message: Message): void {
    const currentUser = this.userService.getCurrentUser();
    if (!currentUser || message.receiverId !== currentUser.id ||
      message.isRead === true || message.read === true) {
      return;
    }

    if (message.id) {
      this.messagingService.markAsRead(message.id).subscribe({
        next: () => {
          message.isRead = true;
          message.read = true;
        },
        error: (err) => {
          console.error('Error marking message as read:', err);
        }
      });
    }
  }

  getOtherParticipantName(): string {
    return this.otherParticipantName || 'Loading...';
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

  refreshConversation(): void {
    this.loadConversation();
  }
}
