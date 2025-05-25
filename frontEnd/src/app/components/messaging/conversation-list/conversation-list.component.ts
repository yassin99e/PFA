// src/app/components/messaging/conversation-list/conversation-list.component.ts
import { Component, OnInit, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { MessagingService, Conversation } from '../../../services/messaging.service';
import { UserService } from '../../../services/user.service';
import { forkJoin, Subscription } from 'rxjs';

interface ConversationWithName extends Conversation {
  otherParticipantName?: string;
}

@Component({
  selector: 'app-conversation-list',
  templateUrl: './conversation-list.component.html',
  styleUrls: ['./conversation-list.component.css'],
  standalone: false
})
export class ConversationListComponent implements OnInit, OnDestroy {
  conversations: ConversationWithName[] = [];
  loading = true;
  error: string | null = null;

  private newMessageSubscription?: Subscription;

  constructor(
    private messagingService: MessagingService,
    private userService: UserService,
    private router: Router,
    private cdr: ChangeDetectorRef // Add ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadConversations();

    // Subscribe to new messages to update list
    this.newMessageSubscription = this.messagingService.newMessage$.subscribe(message => {
      if (message) {
        // Use setTimeout to avoid change detection errors
        setTimeout(() => {
          this.loadConversations();
        }, 0);
      }
    });
  }

  ngOnDestroy(): void {
    if (this.newMessageSubscription) {
      this.newMessageSubscription.unsubscribe();
    }
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
          this.cdr.detectChanges(); // Manually trigger change detection
        }
      });
  }

  private loadParticipantNames(currentUserId: number): void {
    if (this.conversations.length === 0) {
      this.loading = false;
      this.cdr.detectChanges(); // Manually trigger change detection
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
        this.cdr.detectChanges(); // Manually trigger change detection
      },
      error: (err) => {
        console.error('Error loading participant names:', err);
        // Fallback to User IDs if name loading fails
        this.conversations.forEach((conversation, index) => {
          const otherUserId = otherParticipantIds[index];
          conversation.otherParticipantName = `User ${otherUserId}`;
        });
        this.loading = false;
        this.cdr.detectChanges(); // Manually trigger change detection
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

  // Fix for change detection error - use getter methods
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

  // Cache the online status to avoid repeated random calls
  private onlineStatusCache = new Map<number, boolean>();

  isUserOnline(conversation: Conversation): boolean {
    const currentUser = this.userService.getCurrentUser();
    if (!currentUser) return false;

    const otherUserId = currentUser.id === conversation.participantOneId ?
      conversation.participantTwoId : conversation.participantOneId;

    // Return cached status if available
    if (this.onlineStatusCache.has(otherUserId)) {
      return this.onlineStatusCache.get(otherUserId)!;
    }

    // Generate random status and cache it
    const isOnline = Math.random() > 0.5;
    this.onlineStatusCache.set(otherUserId, isOnline);
    return isOnline;
  }
}
