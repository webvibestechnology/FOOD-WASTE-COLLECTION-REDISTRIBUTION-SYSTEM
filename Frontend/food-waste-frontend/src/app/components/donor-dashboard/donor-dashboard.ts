<<<<<<< HEAD
// @ts-ignore Angular types are provided by the project's installed dependencies.
import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
=======
import { Component, OnInit, OnDestroy } from '@angular/core';
import { DatePipe, CommonModule } from '@angular/common';
>>>>>>> 64475a0 (complete)
import { RouterLink } from '@angular/router';
import { Donation } from '../../services/donation';
import { FoodRequest } from '../../services/food-request';
import { Auth } from '../../services/auth';
<<<<<<< HEAD
import { LoadingSpinnersComponent } from '../../shared loading-spinnes/loading-spinner/loading-spinners';
=======
import { LoadingSpinner } from '../../shared/loading-spinner/loading-spinner';
>>>>>>> 64475a0 (complete)

@Component({
  selector: 'app-donor-dashboard',
  standalone: true,
  imports: [
    RouterLink,
    DatePipe,
<<<<<<< HEAD
    LoadingSpinnersComponent
=======
    CommonModule,
    LoadingSpinner
>>>>>>> 64475a0 (complete)
  ],
  templateUrl: './donor-dashboard.html',
  styleUrl: './donor-dashboard.css'
})
export class DonorDashboardComponent implements OnInit, OnDestroy {

  donations: any[] = [];
  incomingRequests: any[] = [];
  isLoading: boolean = false;
  actionMessage: string = '';
  actionError: string = '';
  processingRequestId: number | null = null;
  private refreshInterval: any;

  constructor(
    private donationService: Donation,
    private foodRequestService: FoodRequest,
    private authService: Auth
  ) {}

  ngOnInit(): void {
    this.isLoading = true;
    this.loadAllData();

    // Auto-refresh every 15 seconds to show real-time updates
    this.refreshInterval = setInterval(() => {
      this.loadAllData();
    }, 15000);
  }

  ngOnDestroy(): void {
    if (this.refreshInterval) {
      clearInterval(this.refreshInterval);
    }
  }

  loadAllData(): void {
    this.loadDonations();
    this.loadIncomingRequests();
  }

  loadDonations(): void {
    this.donationService.getMyDonations().subscribe({
      next: (response) => {
        this.donations = response || [];
        this.isLoading = false;
      },
      error: () => {
        this.donations = [];
        this.isLoading = false;
      }
    });
  }

  loadIncomingRequests(): void {
    this.foodRequestService.getIncomingRequests().subscribe({
      next: (requests) => {
        this.incomingRequests = requests || [];
      },
      error: (err) => {
        console.error('Error loading incoming food requests:', err);
      }
    });
  }

  get userName(): string {
    const user = this.authService.getCurrentUser();
    return user?.name || 'Donor';
  }

  get totalDonations(): number {
    return this.donations.length;
  }

  get pendingDonations(): any[] {
    return this.donations.filter(
      donation => donation.status === 'PENDING'
    );
  }

  get acceptedDonations(): any[] {
    return this.donations.filter(
      donation => donation.status === 'ACCEPTED'
    );
  }

  get deliveredDonations(): any[] {
    return this.donations.filter(
      donation =>
        donation.status === 'DELIVERED' ||
        donation.status === 'COMPLETED' ||
        donation.status === 'DISTRIBUTED'
    );
  }

  get recentDonations(): any[] {
    return this.donations.slice(-5).reverse();
  }

  get pendingIncomingRequests(): any[] {
    return this.incomingRequests.filter(req => req.status === 'PENDING');
  }

  approveRequest(requestId: number): void {
    this.processingRequestId = requestId;
    this.actionMessage = '';
    this.actionError = '';

    this.foodRequestService.approveRequest(requestId).subscribe({
      next: () => {
        this.processingRequestId = null;
        this.actionMessage = 'Request approved and quantity successfully allocated! Pickup has been scheduled.';
        this.loadAllData();
        setTimeout(() => { this.actionMessage = ''; }, 5000);
      },
      error: (err: any) => {
        this.processingRequestId = null;
        this.actionError = err?.error?.message || 'Failed to approve request. Please check remaining quantity.';
        setTimeout(() => { this.actionError = ''; }, 5000);
      }
    });
  }

  rejectRequest(requestId: number): void {
    this.processingRequestId = requestId;
    this.actionMessage = '';
    this.actionError = '';

    this.foodRequestService.rejectRequest(requestId).subscribe({
      next: () => {
        this.processingRequestId = null;
        this.actionMessage = 'Request rejected. The food donation remains available for other NGOs.';
        this.loadAllData();
        setTimeout(() => { this.actionMessage = ''; }, 5000);
      },
      error: (err: any) => {
        this.processingRequestId = null;
        this.actionError = err?.error?.message || 'Failed to reject request.';
        setTimeout(() => { this.actionError = ''; }, 5000);
      }
    });
  }
}
