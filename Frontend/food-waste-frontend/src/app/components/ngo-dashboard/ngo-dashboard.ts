import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule, DatePipe } from '@angular/common';
import { FoodRequest } from '../../services/food-request';
import { Auth } from '../../services/auth';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-ngo-dashboard',
  standalone: true,
<<<<<<< HEAD
  imports: [RouterLink, DatePipe],
=======
  imports: [RouterLink, CommonModule, DatePipe],
>>>>>>> 64475a0 (complete)
  templateUrl: './ngo-dashboard.html',
  styleUrl: './ngo-dashboard.css'
})
export class NgoDashboardComponent implements OnInit {

  requests: any[] = [];
  currentUser: any;
  private refreshInterval: any;

  constructor(
    private foodRequestService: FoodRequest,
    private authService: Auth
  ) {}

  ngOnInit(): void {
    this.currentUser = {
      name: localStorage.getItem('name'),
      email: localStorage.getItem('email')
    };

    this.loadRequests();
    
    // Auto-refresh every 10 seconds to show real-time updates
    this.refreshInterval = setInterval(() => {
      this.loadRequests();
    }, 10000);
  }

  ngOnDestroy(): void {
    if (this.refreshInterval) {
      clearInterval(this.refreshInterval);
    }
  }

  loadRequests(): void {
    this.foodRequestService.getMyRequests().subscribe({
      next: (data: any) => {
        this.requests = data;
      },
      error: (error: any) => {
        console.error('Error loading requests:', error);
      }
    });
  }

  get totalRequests(): number {
    return this.requests.length;
  }

  get pendingRequests(): number {
    return this.requests.filter(
      request => request.status === 'PENDING'
    ).length;
  }

  get approvedRequests(): number {
    return this.requests.filter(
      request => request.status === 'APPROVED' ||
                 request.status === 'ACCEPTED'
    ).length;
  }

  get fulfilledRequests(): number {
    return this.requests.filter(
      request => request.status === 'COMPLETED' ||
                 request.status === 'COLLECTED'
    ).length;
  }

  get recentRequests(): any[] {
    return this.requests.slice(0, 5);
  }
}