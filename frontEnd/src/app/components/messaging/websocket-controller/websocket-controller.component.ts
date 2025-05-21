// src/app/components/messaging/websocket-controller/websocket-controller.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { MessagingService } from '../../../services/messaging.service';
import { UserService } from '../../../services/user.service';

@Component({
  selector: 'app-websocket-controller',
  template: '',
  standalone: false
})
export class WebsocketControllerComponent implements OnInit, OnDestroy {

  constructor(
    private messagingService: MessagingService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    const currentUser = this.userService.getCurrentUser();
    if (currentUser) {
      // Connect to WebSocket
      this.messagingService.connect(currentUser.id);

      // Get unread count
      this.messagingService.getUnreadCount(currentUser.id).subscribe();
    }
  }

  ngOnDestroy(): void {
    this.messagingService.disconnect();
  }
}
