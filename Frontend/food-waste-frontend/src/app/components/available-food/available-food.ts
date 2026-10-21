// Angular dependencies may be unavailable while the project is being installed.
// @ts-ignore
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule, DatePipe } from '@angular/common';
import { Donation } from '../../services/donation';
import { FoodRequest } from '../../services/food-request';

@Component({
  selector: 'app-available-food',
  standalone: true,
  imports: [FormsModule, CommonModule, DatePipe],
  templateUrl: './available-food.html',
  styleUrl: './available-food.css'
})
export class AvailableFoodComponent implements OnInit {

  donations: any[] = [];
  filterCategory: string = '';
  searchTerm: string = '';
  successMessage: string = '';
  errorMessage: string = '';
  isLoading: boolean = false;

  // Request Modal State
  isRequestModalOpen: boolean = false;
  selectedDonation: any = null;
  requestQuantity: number = 1;
  beneficiaryCount: number = 1;
  deliveryNotes: string = '';
  isSubmitting: boolean = false;

  constructor(
    private donationService: Donation,
    private foodRequestService: FoodRequest
  ) {}

  ngOnInit(): void {
    this.loadDonations();
  }

  loadDonations(): void {
    this.isLoading = true;
    this.donationService.getAllDonations().subscribe({
      next: (data: any) => {
        // Filter out expired or cancelled donations
        const now = new Date();
        this.donations = (data || []).filter((donation: any) => {
          const isAvailableStatus =
            donation.status === 'AVAILABLE' ||
            donation.status === 'PENDING' ||
            donation.status === 'PARTIALLY_ALLOCATED';
          const notExpired = donation.expiryTime ? new Date(donation.expiryTime) > now : true;
          const hasRemaining = this.getRemainingQuantity(donation) > 0;
          return isAvailableStatus && notExpired && hasRemaining;
        });
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error loading donations:', error);
        this.isLoading = false;
      }
    });
  }

  getRemainingQuantity(donation: any): number {
    if (!donation) return 0;
    const total = donation.quantity || 0;
    const allocated = donation.allocatedQuantity || 0;
    return Math.max(0, total - allocated);
  }

  get filteredDonations(): any[] {
    return this.donations.filter(donation => {
      const matchesCategory = !this.filterCategory || donation.category === this.filterCategory;
      const matchesSearch = !this.searchTerm ||
        (donation.title && donation.title.toLowerCase().includes(this.searchTerm.toLowerCase())) ||
        (donation.pickupAddress && donation.pickupAddress.toLowerCase().includes(this.searchTerm.toLowerCase())) ||
        (donation.dietaryInfo && donation.dietaryInfo.toLowerCase().includes(this.searchTerm.toLowerCase()));
      return matchesCategory && matchesSearch;
    });
  }

  openRequestModal(donation: any): void {
    this.selectedDonation = donation;
    this.requestQuantity = this.getRemainingQuantity(donation);
    this.beneficiaryCount = 10;
    this.deliveryNotes = '';
    this.errorMessage = '';
    this.isRequestModalOpen = true;
  }

  closeRequestModal(): void {
    this.isRequestModalOpen = false;
    this.selectedDonation = null;
    this.errorMessage = '';
  }

  submitRequest(): void {
    if (!this.selectedDonation) return;

    const remaining = this.getRemainingQuantity(this.selectedDonation);
    if (this.requestQuantity <= 0 || this.requestQuantity > remaining) {
      this.errorMessage = `Requested quantity must be between 1 and ${remaining} ${this.selectedDonation.quantityUnit}.`;
      return;
    }

    if (this.beneficiaryCount <= 0) {
      this.errorMessage = 'Beneficiary count must be at least 1 person.';
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';

    this.foodRequestService.createRequest(
      this.selectedDonation.id,
      this.deliveryNotes,
      this.requestQuantity,
      this.beneficiaryCount,
      this.deliveryNotes
    ).subscribe({
      next: () => {
        this.successMessage = `Food request for "${this.selectedDonation.title}" submitted successfully! The donor has been notified to review and allocate.`;
        this.isSubmitting = false;
        this.closeRequestModal();
        this.loadDonations();

        setTimeout(() => {
          this.successMessage = '';
        }, 5000);
      },
      error: (error: any) => {
        this.isSubmitting = false;
        const serverMsg = error?.error?.message || error?.error || error?.message;
        this.errorMessage = serverMsg || 'Failed to submit request. Please ensure your NGO is verified and try again.';
      }
    });
  }
}
