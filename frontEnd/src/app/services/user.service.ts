import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap, BehaviorSubject, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

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

  // Cache for user names to avoid repeated API calls
  private userNameCache = new Map<number, string>();

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
    this.userNameCache.clear(); // Clear cache on logout

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

  // Get all candidates
  getAllCandidates(): Observable<any[]> {
    return this.http.get<any[]>(`${API_URL}/candidate/all`);
  }

  // Get all recruiters
  getAllRecruiters(): Observable<any[]> {
    return this.http.get<any[]>(`${API_URL}/recruiter/all`);
  }

  // Get user by ID
  getUserById(userId: number): Observable<UserData> {
    return this.http.get<UserData>(`${API_URL}/users/${userId}`);
  }

  // NEW METHOD: Get user name by ID with caching
  getUserNameById(userId: number): Observable<string> {
    // Check cache first
    if (this.userNameCache.has(userId)) {
      return of(this.userNameCache.get(userId)!);
    }

    // If not in cache, fetch from API
    return this.getUserById(userId).pipe(
      map(user => {
        const name = user.fullName || user.email.split('@')[0] || `User ${userId}`;
        // Cache the result
        this.userNameCache.set(userId, name);
        return name;
      }),
      catchError(error => {
        console.error(`Error fetching user ${userId}:`, error);
        const fallbackName = `User ${userId}`;
        // Cache the fallback name to avoid repeated failed requests
        this.userNameCache.set(userId, fallbackName);
        return of(fallbackName);
      })
    );
  }

  // NEW METHOD: Preload user names for better performance
  preloadUserNames(userIds: number[]): void {
    const uncachedIds = userIds.filter(id => !this.userNameCache.has(id));

    uncachedIds.forEach(userId => {
      this.getUserNameById(userId).subscribe(); // This will cache the names
    });
  }

  // NEW METHOD: Clear user cache (useful for refresh)
  clearUserCache(): void {
    this.userNameCache.clear();
  }
}
