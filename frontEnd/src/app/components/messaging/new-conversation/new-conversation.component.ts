// src/app/components/messaging/new-conversation/new-conversation.component.ts
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MessagingService } from '../../../services/messaging.service';
import { UserService } from '../../../services/user.service';


interface Candidate {
  id: number;
  fullName: string;
  profile: string;
  technologies?: string[];
}

@Component({
  selector: 'app-new-conversation',
  templateUrl: './new-conversation.component.html',
  styleUrls: ['./new-conversation.component.css'],
  standalone: false
})
export class NewConversationComponent implements OnInit {
  candidates: Candidate[] = [];
  filteredCandidates: Candidate[] = [];
  searchQuery = '';
  loading = true;
  error: string | null = null;

  constructor(
    private messagingService: MessagingService,
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Check if user is a recruiter
    const currentUser = this.userService.getCurrentUser();
    if (!currentUser || currentUser.role !== 'RECRUITER') {
      this.router.navigate(['/messaging']);
      return;
    }

    this.loadCandidates();
  }

  protected loadCandidates(): void {
    this.loading = true;
    // Using userService to fetch candidates from UserMicroService
    this.userService.getAllCandidates().subscribe({
      next: (data) => {
        this.candidates = data.map(c => ({
          id: c.id,
          fullName: c.fullName || 'Unknown',
          profile: c.profile || 'No profile',
          technologies: c.technologies || []
        }));
        this.filteredCandidates = [...this.candidates];
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading candidates:', err);
        this.error = 'Failed to load candidates. Please try again.';
        this.loading = false;
      }
    });
  }

  filterCandidates(): void {
    if (!this.searchQuery) {
      this.filteredCandidates = [...this.candidates];
      return;
    }

    const query = this.searchQuery.toLowerCase();
    this.filteredCandidates = this.candidates.filter(
      c => c.fullName.toLowerCase().includes(query) ||
        c.profile.toLowerCase().includes(query) ||
        (c.technologies && c.technologies.some(tech => tech.toLowerCase().includes(query)))
    );
  }

  startConversation(candidateId: number): void {
    const currentUser = this.userService.getCurrentUser();
    if (!currentUser) {
      this.router.navigate(['/login']);
      return;
    }

    this.messagingService.startConversation(currentUser.id, candidateId).subscribe({
      next: (conversation) => {
        this.router.navigate(['/messaging/conversation', conversation.id]);
      },
      error: (err) => {
        console.error('Error starting conversation:', err);
        this.error = 'Failed to start conversation. Please try again.';
      }
    });
  }
}
