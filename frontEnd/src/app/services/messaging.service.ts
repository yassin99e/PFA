// src/app/services/messaging.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const API_URL = 'http://localhost:8080/MESSAGINGSERVICE/api';
const WS_URL = 'http://localhost:8082/ws';

export interface Message {
  id?: number;
  conversationId: number;
  senderId: number;
  receiverId?: number;
  content: string;
  senderRole: string;
  timestamp?: Date;
  isRead?: boolean;
}

export interface Conversation {
  id: number;
  participantOneId: number; // Candidate
  participantTwoId: number; // Recruiter
  startedAt: Date;
  messages: Message[];
}

@Injectable({
  providedIn: 'root'
})
export class MessagingService {
  private stompClient?: Client;
  private currentUserId?: number;
  private isConnected = false;

  private unreadMessagesSubject = new BehaviorSubject<number>(0);
  public unreadMessages$ = this.unreadMessagesSubject.asObservable();

  private newMessageSubject = new BehaviorSubject<Message | null>(null);
  public newMessage$ = this.newMessageSubject.asObservable();

  private readReceiptSubject = new BehaviorSubject<any>(null);
  public readReceipt$ = this.readReceiptSubject.asObservable();

  constructor(private http: HttpClient) { }

  // Connect to WebSocket server
  public connect(userId: number): Promise<void> {
    return new Promise((resolve, reject) => {
      if (this.isConnected && this.currentUserId === userId) {
        resolve();
        return;
      }

      this.currentUserId = userId;

      this.stompClient = new Client({
        webSocketFactory: () => new SockJS(WS_URL),
        debug: function(str) {
          console.log('STOMP: ' + str);
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000
      });

      this.stompClient.onConnect = () => {
        console.log('WebSocket Connected for user:', userId);
        this.isConnected = true;

        // Subscribe to personal messages
        this.stompClient?.subscribe('/user/' + userId + '/queue/messages', message => {
          console.log('Received message:', message.body);
          const receivedMessage = JSON.parse(message.body) as Message;
          // Convert timestamp string to Date object
          if (receivedMessage.timestamp) {
            receivedMessage.timestamp = new Date(receivedMessage.timestamp);
          }
          this.newMessageSubject.next(receivedMessage);

          // Update unread count only if this message is for current user
          if (receivedMessage.receiverId === userId) {
            this.updateUnreadCount(1);
          }
        });

        // Subscribe to read receipts
        this.stompClient?.subscribe('/user/' + userId + '/queue/read-receipt', receipt => {
          console.log('Received read receipt:', receipt.body);
          const readData = JSON.parse(receipt.body);
          this.readReceiptSubject.next(readData);
        });

        // Send user connection message
        this.stompClient?.publish({
          destination: '/app/chat.addUser',
          body: JSON.stringify({ userId: userId.toString() })
        });

        resolve();
      };

      this.stompClient.onStompError = (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
        this.isConnected = false;
        reject(frame);
      };

      this.stompClient.onWebSocketError = (error) => {
        console.error('WebSocket error:', error);
        this.isConnected = false;
        reject(error);
      };

      this.stompClient.activate();
    });
  }

  // Disconnect from WebSocket server
  public disconnect(): void {
    if (this.stompClient && this.isConnected) {
      this.stompClient.deactivate();
      this.isConnected = false;
      console.log('WebSocket Disconnected');
    }
  }

  // Check if WebSocket is connected
  public isWebSocketConnected(): boolean {
    return this.isConnected && this.stompClient?.connected === true;
  }

  // Send a message
  public sendMessage(message: Message): Observable<Message> {
    // Always try WebSocket first if connected
    if (this.isWebSocketConnected()) {
      console.log('Sending message via WebSocket:', message);

      // Determine receiver ID based on conversation participants
      this.getConversation(message.conversationId).subscribe(conversation => {
        const receiverId = this.currentUserId === conversation.participantOneId ?
          conversation.participantTwoId : conversation.participantOneId;

        const messageToSend = { ...message, receiverId };

        this.stompClient?.publish({
          destination: '/app/chat.sendMessage',
          body: JSON.stringify(messageToSend)
        });
      });

      return new Observable(observer => {
        observer.next(message);
        observer.complete();
      });
    } else {
      // Fallback to REST API
      console.log('Sending message via REST API:', message);
      return this.http.post<Message>(`${API_URL}/messages`, message);
    }
  }

  // Mark message as read
  public markAsRead(messageId: number): Observable<void> {
    if (this.isWebSocketConnected()) {
      this.stompClient?.publish({
        destination: '/app/chat.markRead',
        body: JSON.stringify({ messageId, userId: this.currentUserId })
      });
      this.updateUnreadCount(-1); // Decrement unread count
      return new Observable(observer => {
        observer.next();
        observer.complete();
      });
    }
    return this.http.put<void>(`${API_URL}/messages/${messageId}/read`, {});
  }

  // Mark all messages in a conversation as read
  public markConversationAsRead(conversationId: number): Observable<void> {
    if (this.isWebSocketConnected()) {
      this.stompClient?.publish({
        destination: '/app/chat.markRead',
        body: JSON.stringify({
          conversationId,
          userId: this.currentUserId
        })
      });
      return new Observable(observer => {
        observer.next();
        observer.complete();
      });
    }
    return this.http.put<void>(`${API_URL}/messages/conversation/${conversationId}/read?userId=${this.currentUserId}`, {});
  }

  // Get all conversations for the current user
  public getConversations(userId: number, role: string): Observable<Conversation[]> {
    return this.http.get<Conversation[]>(`${API_URL}/conversations/user/${userId}?role=${role}`);
  }

  // Get a specific conversation
  public getConversation(id: number): Observable<Conversation> {
    return this.http.get<Conversation>(`${API_URL}/messages/conversation/detail/${id}`);
  }

  // Get messages for a conversation
  public getMessages(conversationId: number): Observable<Message[]> {
    return this.http.get<Message[]>(`${API_URL}/messages/conversation/${conversationId}`);
  }

  // Start a new conversation (only for recruiters)
  public startConversation(recruiterId: number, candidateId: number): Observable<Conversation> {
    return this.http.post<Conversation>(
      `${API_URL}/conversations/start?recruiterId=${recruiterId}&candidateId=${candidateId}`,
      {}
    );
  }

  // Update unread messages count
  private updateUnreadCount(change: number): void {
    const current = this.unreadMessagesSubject.value;
    this.unreadMessagesSubject.next(Math.max(0, current + change));
  }

  // Get the count of unread messages
  getUnreadCount(userId: number): Observable<number> {
    const url = `${API_URL}/messages/unread/count`;
    console.log('Request URL:', url);
    console.log('User ID:', userId);

    return this.http.get<number>(url, {
      params: { userId: userId.toString() }
    }).pipe(
      tap(
        data => console.log('Response data:', data),
        error => console.error('Error in getUnreadCount:', error)
      )
    );
  }
}
