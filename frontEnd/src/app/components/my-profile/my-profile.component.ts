import { Component, OnInit } from '@angular/core';
import { UserService, UserData } from '../../services/user.service';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-my-profile',
  templateUrl: './my-profile.component.html',
  styleUrls: ['./my-profile.component.css'],
  standalone: false
})
export class MyProfileComponent implements OnInit {
  currentUser: UserData | null = null;
  isEditing = false;
  hidePassword = true;

  // Form data
  profileData: any = {
    email: '',
    fullName: '',
    password: '',
    // Candidate specific fields
    profile: '',
    diploma: '',
    phone: '',
    yearsOfExperience: 0,
    technologies: [] as string[],
    interestedProfiles: [] as string[],
    // Recruiter specific fields
    company: '',
    department: ''
  };

  // String representations for editing
  technologiesInput = '';
  interestedProfilesInput = '';

  errorMessage = '';
  successMessage = '';
  loading = false;

  private readonly API_URL = 'http://localhost:8080/USERMICROSERVICE/api';

  constructor(
    private userService: UserService,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.loadUserProfile();
  }

  loadUserProfile(): void {
    this.currentUser = this.userService.getCurrentUser();
    if (!this.currentUser) {
      this.router.navigate(['/login']);
      return;
    }

    this.loading = true;
    // Load detailed user data based on role
    if (this.currentUser.role === 'CANDIDATE') {
      this.loadCandidateProfile();
    } else if (this.currentUser.role === 'RECRUITER') {
      this.loadRecruiterProfile();
    }
  }

  loadCandidateProfile(): void {
    this.http.get<any>(`${this.API_URL}/candidate/${this.currentUser!.id}`)
      .subscribe({
        next: (data) => {
          this.profileData = { ...data };
          this.technologiesInput = data.technologies ? data.technologies.join(', ') : '';
          this.interestedProfilesInput = data.interestedProfiles ? data.interestedProfiles.join(', ') : '';
          this.loading = false;
        },
        error: (error) => {
          console.error('Error loading candidate profile:', error);
          this.errorMessage = 'Failed to load profile data';
          this.loading = false;
        }
      });
  }

  loadRecruiterProfile(): void {
    this.http.get<any>(`${this.API_URL}/recruiter/${this.currentUser!.id}`)
      .subscribe({
        next: (data) => {
          this.profileData = { ...data };
          this.loading = false;
        },
        error: (error) => {
          console.error('Error loading recruiter profile:', error);
          this.errorMessage = 'Failed to load profile data';
          this.loading = false;
        }
      });
  }

  toggleEdit(): void {
    this.isEditing = !this.isEditing;
    this.errorMessage = '';
    this.successMessage = '';
  }

  cancelEdit(): void {
    this.isEditing = false;
    this.loadUserProfile(); // Reload original data
    this.errorMessage = '';
    this.successMessage = '';
  }

  saveProfile(): void {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    // Prepare data for update
    const updateData = { ...this.profileData };

    // Remove password if empty (don't update password if not provided)
    if (!updateData.password || updateData.password.trim() === '') {
      delete updateData.password;
    }

    if (this.currentUser!.role === 'CANDIDATE') {
      // Process technologies and interested profiles
      updateData.technologies = this.technologiesInput
        .split(',')
        .map(t => t.trim())
        .filter(t => t);
      updateData.interestedProfiles = this.interestedProfilesInput
        .split(',')
        .map(p => p.trim())
        .filter(p => p);
    }

    const updateObservable = this.currentUser!.role === 'CANDIDATE'
      ? this.http.put(`${this.API_URL}/candidate/update/${this.currentUser!.id}`, updateData, { responseType: 'text' })
      : this.http.put(`${this.API_URL}/recruiter/update/${this.currentUser!.id}`, updateData, { responseType: 'text' });

    updateObservable.subscribe({
      next: (response) => {
        this.successMessage = 'Profile updated successfully!';
        this.isEditing = false;
        this.loading = false;

        // Update current user data in localStorage if basic info changed
        if (updateData.email !== this.currentUser!.email ||
          updateData.fullName !== this.currentUser!.fullName) {
          const updatedUser = {
            ...this.currentUser!,
            email: updateData.email,
            fullName: updateData.fullName
          };
          localStorage.setItem('userData', JSON.stringify(updatedUser));
          // Update the current user subject
          this.userService['currentUserSubject'].next(updatedUser);
          this.currentUser = updatedUser;
        }

        // Clear success message after 3 seconds
        setTimeout(() => {
          this.successMessage = '';
        }, 3000);
      },
      error: (error) => {
        console.error('Error updating profile:', error);
        this.errorMessage = 'Failed to update profile. Please try again.';
        this.loading = false;
      }
    });
  }

  isCandidate(): boolean {
    return this.currentUser?.role === 'CANDIDATE';
  }

  isRecruiter(): boolean {
    return this.currentUser?.role === 'RECRUITER';
  }
}
