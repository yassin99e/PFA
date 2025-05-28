import { Component, OnInit } from '@angular/core';
import { Chart, registerables } from 'chart.js';
import { AnalyticsService, AdminDashboardData, RecruiterDashboardData, CandidateDashboardData } from '../../services/analytics.service';
import { UserService } from '../../services/user.service';

// Register Chart.js components
Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  standalone: false
})
export class DashboardComponent implements OnInit {

  // Loading states
  loading = true;
  error: string | null = null;

  // User info
  currentUser: any;
  userRole: string = '';

  // Dashboard data
  adminData: AdminDashboardData | null = null;
  recruiterData: RecruiterDashboardData | null = null;
  candidateData: CandidateDashboardData | null = null;

  // Charts
  userRoleChart: Chart | null = null;
  scoreDistributionChart: Chart | null = null;
  trendingJobsChart: Chart | null = null;
  trendingTechChart: Chart | null = null;

  constructor(
    private analyticsService: AnalyticsService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.loadUserInfo();
    this.loadDashboardData();
  }

  loadUserInfo(): void {
    this.currentUser = this.userService.getCurrentUser();
    this.userRole = this.userService.getUserRole();
  }

  loadDashboardData(): void {
    this.loading = true;
    this.error = null;

    if (this.userRole === 'RECRUITER') {
      // Recruiter sees both admin and recruiter dashboards
      this.loadAdminData();
      this.loadRecruiterData();
    } else if (this.userRole === 'CANDIDATE') {
      // Candidate sees only candidate dashboard
      this.loadCandidateData();
    } else {
      // Fallback - load admin data
      this.loadAdminData();
    }
  }

  loadAdminData(): void {
    this.analyticsService.getAdminDashboard().subscribe({
      next: (data) => {
        this.adminData = data;
        setTimeout(() => this.createUserRoleChart(), 100);
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load admin dashboard data';
        this.loading = false;
        console.error('Admin dashboard error:', err);
      }
    });
  }

  loadRecruiterData(): void {
    if (this.currentUser?.id) {
      this.analyticsService.getRecruiterDashboard(this.currentUser.id).subscribe({
        next: (data) => {
          this.recruiterData = data;
          setTimeout(() => this.createScoreDistributionChart(), 100);
        },
        error: (err) => {
          console.error('Recruiter dashboard error:', err);
        }
      });
    }
  }

  loadCandidateData(): void {
    this.analyticsService.getCandidateDashboard().subscribe({
      next: (data) => {
        this.candidateData = data;
        setTimeout(() => {
          this.createTrendingJobsChart();
          this.createTrendingTechChart();
        }, 100);
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load candidate dashboard data';
        this.loading = false;
        console.error('Candidate dashboard error:', err);
      }
    });
  }

  createUserRoleChart(): void {
    if (!this.adminData) return;

    const ctx = document.getElementById('userRoleChart') as HTMLCanvasElement;
    if (!ctx) return;

    // Destroy existing chart
    if (this.userRoleChart) {
      this.userRoleChart.destroy();
    }

    this.userRoleChart = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: this.adminData.userRoleChart.labels,
        datasets: [{
          data: this.adminData.userRoleChart.data,
          backgroundColor: [
            '#3f51b5',
            '#ff4081'
          ],
          borderWidth: 2,
          borderColor: '#fff'
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: {
            position: 'bottom'
          },
          title: {
            display: true,
            text: 'User Distribution'
          }
        }
      }
    });
  }

  createScoreDistributionChart(): void {
    if (!this.recruiterData) return;

    const ctx = document.getElementById('scoreDistributionChart') as HTMLCanvasElement;
    if (!ctx) return;

    // Destroy existing chart
    if (this.scoreDistributionChart) {
      this.scoreDistributionChart.destroy();
    }

    this.scoreDistributionChart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: this.recruiterData.scoreDistribution.labels,
        datasets: [{
          label: 'Applications',
          data: this.recruiterData.scoreDistribution.data,
          backgroundColor: [
            '#f44336', // 0-20 (red)
            '#ff9800', // 21-40 (orange)
            '#ffeb3b', // 41-60 (yellow)
            '#4caf50', // 61-80 (light green)
            '#2e7d32'  // 81-100 (dark green)
          ],
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        plugins: {
          title: {
            display: true,
            text: 'Match Score Distribution'
          }
        },
        scales: {
          y: {
            beginAtZero: true
          }
        }
      }
    });
  }

  createTrendingJobsChart(): void {
    if (!this.candidateData) return;

    const ctx = document.getElementById('trendingJobsChart') as HTMLCanvasElement;
    if (!ctx) return;

    // Destroy existing chart
    if (this.trendingJobsChart) {
      this.trendingJobsChart.destroy();
    }

    this.trendingJobsChart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: this.candidateData.trendingJobTitles.labels,
        datasets: [{
          label: 'Job Openings',
          data: this.candidateData.trendingJobTitles.data,
          backgroundColor: '#3f51b5',
          borderColor: '#3f51b5',
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        indexAxis: 'y',
        plugins: {
          title: {
            display: true,
            text: 'Most Demanded Job Titles'
          }
        },
        scales: {
          x: {
            beginAtZero: true
          }
        }
      }
    });
  }

  createTrendingTechChart(): void {
    if (!this.candidateData) return;

    const ctx = document.getElementById('trendingTechChart') as HTMLCanvasElement;
    if (!ctx) return;

    // Destroy existing chart
    if (this.trendingTechChart) {
      this.trendingTechChart.destroy();
    }

    this.trendingTechChart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: this.candidateData.trendingTechnologies.labels,
        datasets: [{
          label: 'Demand',
          data: this.candidateData.trendingTechnologies.data,
          backgroundColor: '#ff4081',
          borderColor: '#ff4081',
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        plugins: {
          title: {
            display: true,
            text: 'Most Requested Technologies'
          }
        },
        scales: {
          y: {
            beginAtZero: true
          }
        }
      }
    });
  }

  ngOnDestroy(): void {
    // Clean up charts
    if (this.userRoleChart) this.userRoleChart.destroy();
    if (this.scoreDistributionChart) this.scoreDistributionChart.destroy();
    if (this.trendingJobsChart) this.trendingJobsChart.destroy();
    if (this.trendingTechChart) this.trendingTechChart.destroy();
  }
}
