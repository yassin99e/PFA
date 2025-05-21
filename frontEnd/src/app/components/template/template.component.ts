import { Component, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { MatSidenav } from '@angular/material/sidenav';
import {MessagingService} from '../../services/messaging.service';

@Component({
  selector: 'app-template',
  templateUrl: './template.component.html',
  styleUrls: ['./template.component.css'],
  standalone: false
})
export class TemplateComponent {
  @ViewChild('sidenav') sidenav!: MatSidenav;
  unreadCount = 0;
  constructor(
    private userService: UserService,
    private messagingService: MessagingService,
    private router: Router) {}

  ngOnInit() {
    // Subscribe to unread messages
    this.messagingService.unreadMessages$.subscribe(count => {
      this.unreadCount = count;
    });

    // Load unread count if user is logged in
    if (this.isLoggedIn) {
      const currentUser = this.userService.getCurrentUser();
      if (currentUser) {
        this.loadUnreadCount(currentUser.id);
      }
    }
  }

  private loadUnreadCount(userId: number) {
    this.messagingService.getUnreadCount(userId).subscribe({
      next: (count) => {
        this.unreadCount = count;
      },
      error: (err) => {
        console.error('Error loading unread count:', err);
      }
    });
  }

  get isLoggedIn(): boolean {
    return this.userService.isAuthenticated();
  }

  getUserDisplayName(): string {
    return this.userService.getUserDisplayName();
  }

  getUserEmail(): string {
    return this.userService.getUserEmail();
  }

  getUserRole(): string {
    return this.userService.getUserRole();
  }

  toggleSidenav() {
    this.sidenav.toggle();
  }

  logout() {
    this.userService.logout();
    this.router.navigate(['/login']);
    if (this.sidenav?.opened) {
      this.sidenav.close();
    }
  }
}
