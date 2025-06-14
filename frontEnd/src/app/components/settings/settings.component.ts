import { Component } from '@angular/core';

@Component({
  selector: 'app-settings',
  standalone: false,
  template: `
    <div class="profile-container">
      <h2>Settings</h2>
      <p>Settings page is under construction. Check back soon!</p>
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
export class SettingsComponent {

}

