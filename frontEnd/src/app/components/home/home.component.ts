import { Component, OnInit } from '@angular/core';
import { UserService, UserData } from '../../services/user.service'; // Adjust path as needed
import { Observable } from 'rxjs';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],// or .scss if you're using SCSS
  standalone: false
})
export class HomeComponent implements OnInit {
  currentUser: UserData | null = null;
  userDisplayName: string = '';

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    // Subscribe to user observable to update view reactively
    this.userService.getCurrentUser$().subscribe((user) => {
      this.currentUser = user;
      this.userDisplayName = this.userService.getUserDisplayName();
    });
  }
}
