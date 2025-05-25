import { Component } from '@angular/core';

@Component({
  selector: 'app-my-profile',
  standalone: false,
  template: `
    <div class="profile-container">
      <h2>My Profile</h2>
      <p>Profile page is under construction. Check back soon!</p>
    </div>
  `,
  styles: [`
    .profile-container {
      padding: 24px;
      max-width: 800px;
      margin: 0 auto;
    }
  `]
})
export class MyProfileComponent {

}

