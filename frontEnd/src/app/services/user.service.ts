import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap, BehaviorSubject } from 'rxjs';

import { LoginDTO } from '../models/user-DTOS/login.dto';
import { RecruiterDTO } from '../models/user-DTOS/recruiter.dto';
import { CandidateDTO } from '../models/user-DTOS/candidate.dto';

const API_URL = 'http://localhost:8080/USERMICROSERVICE/api';

// Interface for user data (matching your backend response)
export interface UserData {
  id: number;
  email: string;
  fullName?: string;
  role: 'CANDIDATE' | 'RECRUITER';
}

// Interface for login response (your backend returns the user object directly)
export interface LoginResponse extends UserData {
  // The backend returns user data directly, not wrapped in a "user" property
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private currentUserSubject = new BehaviorSubject<UserData | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  private loggedIn = false;

  constructor(private http: HttpClient) {
    // Check if user data exists in localStorage on service initialization
    this.loadUserFromStorage();
  }

  // Login
  login(credentials: LoginDTO): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${API_URL}/users/login`, credentials).pipe(
      tap((response: LoginResponse) => {
        this.loggedIn = true;

        // Store user data (response IS the user data in your case)
        this.currentUserSubject.next(response);

        // Store in localStorage for session persistence
        localStorage.setItem('userData', JSON.stringify(response));
      })
    );
  }

  // Logout
  logout(): void {
    this.loggedIn = false;
    this.currentUserSubject.next(null);

    // Clear stored data
    localStorage.removeItem('userData');
  }

  // Check if user is authenticated
  isAuthenticated(): boolean {
    return this.loggedIn || this.getCurrentUser() !== null;
  }

  // Get current user data
  getCurrentUser(): UserData | null {
    return this.currentUserSubject.value;
  }

  // Get current user as observable
  getCurrentUser$(): Observable<UserData | null> {
    return this.currentUser$;
  }

  // Get user's display name
  getUserDisplayName(): string {
    const user = this.getCurrentUser();
    if (user) {
      if (user.fullName) return user.fullName;
      return user.email.split('@')[0]; // Use part before @ as fallback
    }
    return '';
  }

  // Get user's email
  getUserEmail(): string {
    const user = this.getCurrentUser();
    return user?.email || '';
  }

  // Get user's role
  getUserRole(): string {
    const user = this.getCurrentUser();
    return user?.role || '';
  }

  // Load user from localStorage
  private loadUserFromStorage(): void {
    const storedUser = localStorage.getItem('userData');
    if (storedUser) {
      try {
        const userData: UserData = JSON.parse(storedUser);
        this.currentUserSubject.next(userData);
        this.loggedIn = true;
      } catch (error) {
        console.error('Error parsing stored user data:', error);
        localStorage.removeItem('userData');
      }
    }
  }

  // Register Candidate
  registerCandidate(candidateData: CandidateDTO): Observable<any> {
    return this.http.post(`${API_URL}/candidate/register`, candidateData);
  }

  // Register Recruiter
  registerRecruiter(recruiterData: RecruiterDTO): Observable<any> {
    return this.http.post(`${API_URL}/recruiter/register`, recruiterData);
  }
}
