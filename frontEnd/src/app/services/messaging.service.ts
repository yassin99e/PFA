// src/app/services/messaging.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Observable, BehaviorSubject, tap} from 'rxjs';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
const API_URL = 'http://localhost:8080/MESSAGINGSERVICE/api';
const WS_URL = 'http://localhost:8080/MESSAGINGSERVICE/ws';

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

    private unreadMessagesSubject = new BehaviorSubject<number>(0);
    public unreadMessages$ = this.unreadMessagesSubject.asObservable();

    private newMessageSubject = new BehaviorSubject<Message | null>(null);
    public newMessage$ = this.newMessageSubject.asObservable();

    constructor(private http: HttpClient) { }

    // Connect to WebSocket server
    public connect(userId: number): void {
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
            // Subscribe to personal messages
            this.stompClient?.subscribe('/user/' + userId + '/queue/messages', message => {
                const receivedMessage = JSON.parse(message.body) as Message;
                this.newMessageSubject.next(receivedMessage);
                this.updateUnreadCount(1); // Increment unread count
            });
        };

        this.stompClient.activate();
    }

    // Disconnect from WebSocket server
    public disconnect(): void {
        if (this.stompClient) {
            this.stompClient.deactivate();
        }
    }

    // Send a message
    public sendMessage(message: Message): Observable<Message> {
        // If WebSocket is connected, send through WebSocket
        if (this.stompClient && this.stompClient.connected) {
            this.stompClient.publish({
                destination: '/app/chat.sendMessage',
                body: JSON.stringify(message)
            });
            return new Observable(observer => {
                observer.next(message);
                observer.complete();
            });
        } else {
            // Otherwise use REST API
            return this.http.post<Message>(`${API_URL}/messages`, message);
        }
    }

    // Mark message as read
    public markAsRead(messageId: number): Observable<void> {
        if (this.stompClient && this.stompClient.connected) {
            this.stompClient.publish({
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
        if (this.stompClient && this.stompClient.connected) {
            this.stompClient.publish({
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
        return this.http.get<Conversation>(`${API_URL}/conversations/${id}`);
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
