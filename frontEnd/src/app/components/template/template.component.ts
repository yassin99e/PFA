import { Component, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { MatSidenav } from '@angular/material/sidenav';

@Component({
  selector: 'app-template',
  templateUrl: './template.component.html',
  styleUrls: ['./template.component.css'],
  standalone: false
})
export class TemplateComponent {
  @ViewChild('sidenav') sidenav!: MatSidenav;

  constructor(private userService: UserService, private router: Router) {}

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
