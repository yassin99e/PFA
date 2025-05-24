// src/app/components/messaging/conversation-list/conversation-list.component.ts
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MessagingService, Conversation } from '../../../services/messaging.service';
import { UserService } from '../../../services/user.service';
import { forkJoin } from 'rxjs';

interface ConversationWithName extends Conversation {
  otherParticipantName?: string;
}

@Component({
  selector: 'app-conversation-list',
  templateUrl: './conversation-list.component.html',
  styleUrls: ['./conversation-list.component.css'],
  standalone: false
})
export class ConversationListComponent implements OnInit {
  conversations: ConversationWithName[] = [];
  loading = true;
  error: string | null = null;

  constructor(
    private messagingService: MessagingService,
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadConversations();

    // Subscribe to new messages to update list
    this.messagingService.newMessage$.subscribe(message => {
      if (message) {
        this.loadConversations();
      }
    });
  }

  protected loadConversations(): void {
    const currentUser = this.userService.getCurrentUser();
    if (!currentUser) {
      this.router.navigate(['/login']);
      return;
    }

    this.loading = true;
    this.messagingService
      .getConversations(currentUser.id, currentUser.role)
      .subscribe({
        next: (data) => {
          this.conversations = data;
          // Load participant names for all conversations
          this.loadParticipantNames(currentUser.id);
        },
        error: (err) => {
          console.error('Error loading conversations:', err);
          this.error = 'Failed to load conversations. Please try again.';
          this.loading = false;
        }
      });
  }

  private loadParticipantNames(currentUserId: number): void {
    if (this.conversations.length === 0) {
      this.loading = false;
      return;
    }

    // Get all unique other participant IDs
    const otherParticipantIds = this.conversations.map(conversation => {
      return currentUserId === conversation.participantOneId ?
        conversation.participantTwoId :
        conversation.participantOneId;
    });

    // Remove duplicates
    const uniqueIds = [...new Set(otherParticipantIds)];

    // Preload all user names to cache them
    this.userService.preloadUserNames(uniqueIds);

    // Create observables for all name requests
    const nameRequests = this.conversations.map((conversation, index) => {
      const otherUserId = otherParticipantIds[index];
      return this.userService.getUserNameById(otherUserId);
    });

    // Execute all requests in parallel
    forkJoin(nameRequests).subscribe({
      next: (names) => {
        // Assign names to conversations
        this.conversations.forEach((conversation, index) => {
          conversation.otherParticipantName = names[index];
        });
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading participant names:', err);
        // Fallback to User IDs if name loading fails
        this.conversations.forEach((conversation, index) => {
          const otherUserId = otherParticipantIds[index];
          conversation.otherParticipantName = `User ${otherUserId}`;
        });
        this.loading = false;
      }
    });
  }

  openConversation(conversationId: number): void {
    this.router.navigate(['/messaging/conversation', conversationId]);
  }

  // For recruiters only
  startNewConversation(): void {
    const currentUser = this.userService.getCurrentUser();
    if (!currentUser || currentUser.role !== 'RECRUITER') {
      return;
    }

    this.router.navigate(['/messaging/new']);
  }

  isRecruiter(): boolean {
    const currentUser = this.userService.getCurrentUser();
    return currentUser !== null && currentUser.role === 'RECRUITER';
  }

  getOtherParticipantName(conversation: ConversationWithName): string {
    return conversation.otherParticipantName || 'Loading...';
  }

  getLastMessage(conversation: Conversation): string {
    if (!conversation.messages || conversation.messages.length === 0) {
      return 'No messages yet';
    }

    const lastMessage = conversation.messages[conversation.messages.length - 1];
    return lastMessage.content.length > 30 ?
      `${lastMessage.content.substring(0, 30)}...` :
      lastMessage.content;
  }

  getLastMessageTime(conversation: Conversation): string {
    if (!conversation.messages || conversation.messages.length === 0) {
      return new Date(conversation.startedAt).toLocaleString();
    }

    const lastMessage = conversation.messages[conversation.messages.length - 1];
    return new Date(lastMessage.timestamp!).toLocaleString();
  }

  hasUnreadMessages(conversation: Conversation): boolean {
    const currentUser = this.userService.getCurrentUser();
    if (!currentUser || !conversation.messages) return false;

    return conversation.messages.some(msg =>
      msg.receiverId === currentUser.id && !msg.isRead
    );
  }

  getUnreadCount(conversation: Conversation): number {
    const currentUser = this.userService.getCurrentUser();
    if (!currentUser || !conversation.messages) return 0;

    return conversation.messages.filter(msg =>
      msg.receiverId === currentUser.id && !msg.isRead
    ).length;
  }

  isUserOnline(conversation: Conversation): boolean {
    // Placeholder - in real app, would check with user status service
    return Math.random() > 0.5; // Random for demo purposes
  }
}
