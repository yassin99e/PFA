import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { LoginDTO } from '../../models/user-DTOS/login.dto';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  standalone: false
})
export class LoginComponent {
  credentials: LoginDTO = { email: '', password: '' };
  errorMessage = '';
  hidePassword = true;
  rememberMe = false;

  constructor(private userService: UserService, private router: Router) {}

  onLogin() {
    this.userService.login(this.credentials)
      .subscribe({
        next: () => this.router.navigate(['/home']),
        error: err => this.errorMessage = err.error || 'Login failed. Please check your credentials and try again.'
      });
  }
}
