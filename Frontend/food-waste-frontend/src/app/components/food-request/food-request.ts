import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FoodRequest } from '../../services/food-request';
import { DatePipe } from '@angular/common';

@Component({
  
  selector: 'app-food-request',
  standalone: true,
<<<<<<< HEAD
  imports: [DatePipe],
=======
  imports: [CommonModule, DatePipe, RouterLink],
>>>>>>> 64475a0 (complete)
  templateUrl: './food-request.html',
  styleUrl: './food-request.css'
})
export class FoodRequestComponent implements OnInit {

  requests: any[] = [];
  isLoading: boolean = false;
  statusMessage: string = '';
  errorMessage: string = '';
  cancellingId: number | null = null;

  constructor(
    private foodRequestService: FoodRequest
  ) {}

  ngOnInit(): void {
    this.loadRequests();
  }

  loadRequests(): void {
    this.isLoading = true;
    this.foodRequestService.getMyRequests().subscribe({
      next: (data: any) => {
        this.requests = data || [];
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error loading requests:', error);
        this.isLoading = false;
      }
    });
  }

  cancelRequest(requestId: number): void {
    if (!confirm('Are you sure you want to cancel this food request? Any allocated food will be returned to surplus.')) {
      return;
    }

    this.cancellingId = requestId;
    this.statusMessage = '';
    this.errorMessage = '';

    this.foodRequestService.cancelRequest(requestId).subscribe({
      next: () => {
        this.cancellingId = null;
        this.statusMessage = 'Request cancelled successfully.';
        this.loadRequests();
        setTimeout(() => { this.statusMessage = ''; }, 4000);
      },
      error: (err: any) => {
        this.cancellingId = null;
        this.errorMessage = err?.error?.message || 'Failed to cancel request.';
        setTimeout(() => { this.errorMessage = ''; }, 4000);
      }
    });
  }
}
