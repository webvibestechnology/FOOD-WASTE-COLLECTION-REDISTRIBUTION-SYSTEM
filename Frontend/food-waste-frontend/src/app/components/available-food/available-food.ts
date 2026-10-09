// Angular dependencies may be unavailable while the project is being installed.
// @ts-ignore
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Donation } from '../../services/donation';
import { FoodRequest } from '../../services/food-request';

@Component({
  selector: 'app-available-food',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './available-food.html',
  styleUrl: './available-food.css'
})
export class AvailableFoodComponent implements OnInit {

  donations: any[] = [];
  filterCategory = '';
  successMessage = '';

  constructor(
    private donationService: Donation,
    private foodRequestService: FoodRequest
  ) {}

  ngOnInit(): void {
    this.loadDonations();
  }

  loadDonations(): void {
    this.donationService.getAllDonations().subscribe({
      next: (data: any) => {
        this.donations = data.filter(
          (donation: any) =>
            donation.status === 'PENDING' ||
            donation.status === 'AVAILABLE'
        );
      },
      error: (error: any) => {
        console.error('Error loading donations:', error);
      }
    });
  }

  get filteredDonations(): any[] {
    if (!this.filterCategory) {
      return this.donations;
    }

    return this.donations.filter(
      donation => donation.category === this.filterCategory
    );
  }

  requestDonation(donationId: number): void {
    this.foodRequestService
      .createRequest(donationId, '')
      .subscribe({
        next: () => {
          this.successMessage = 'Food request submitted successfully.';
          this.loadDonations();

          setTimeout(() => {
            this.successMessage = '';
          }, 3000);
        },
        error: (error: any) => {
          console.error('Error creating request:', error);
        }
      });
  }
}