// @ts-ignore Angular types are provided by the project's installed dependencies.
import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Donation } from '../../services/donation';
import { Auth } from '../../services/auth';
import { LoadingSpinnersComponent } from '../../shared loading-spinnes/loading-spinner/loading-spinners';

@Component({
  selector: 'app-donor-dashboard',
  standalone: true,
  imports: [
    RouterLink,
    DatePipe,
    LoadingSpinnersComponent
  ],
  templateUrl: './donor-dashboard.html',
  styleUrl: './donor-dashboard.css'
})
export class DonorDashboardComponent implements OnInit {

  donations: any[] = [];
  isLoading: boolean = false;

  constructor(
    private donationService: Donation,
    private authService: Auth
  ) {}

  ngOnInit(): void {
    this.isLoading = true;

    this.donationService.getMyDonations().subscribe({
      next: (response) => {
        this.donations = response;
        this.isLoading = false;
      },
      error: () => {
        this.donations = [];
        this.isLoading = false;
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

  get activeDonations(): any[] {
    return this.donations.filter(
      donation =>
        donation.status === 'PENDING' ||
        donation.status === 'ACCEPTED'
    );
  }

  get deliveredDonations(): any[] {
    return this.donations.filter(
      donation => donation.status === 'DELIVERED'
    );
  }

  get recentDonations(): any[] {
    return this.donations.slice(-5).reverse();
  }
}