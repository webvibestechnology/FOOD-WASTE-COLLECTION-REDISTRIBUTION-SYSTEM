import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CommonModule, DatePipe } from '@angular/common';
import { Pickup } from '../../services/pickup';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-pickup-requests',
  standalone: true,
<<<<<<< HEAD
  imports: [FormsModule, RouterLink, DatePipe],
=======
  imports: [FormsModule, RouterLink, CommonModule, DatePipe],
>>>>>>> 64475a0 (complete)
  templateUrl: './pickup-requests.html',
  styleUrl: './pickup-requests.css'
})
export class PickupRequestsComponent implements OnInit {

  activeTab: 'my' | 'available' = 'my';
  myPickups: any[] = [];
  availablePickups: any[] = [];
  filterStatus = '';
  isLoading = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private pickupService: Pickup
  ) {}

  ngOnInit(): void {
    this.loadAllPickups();
  }

  loadAllPickups(): void {
    this.isLoading = true;
    this.pickupService.getMyPickups().subscribe({
      next: (data: any) => {
        this.myPickups = data || [];
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error loading my pickups:', error);
        this.isLoading = false;
      }
    });

    this.pickupService.getAvailablePickups().subscribe({
      next: (data: any) => {
        this.availablePickups = data || [];
      },
      error: (error: any) => {
        console.error('Error loading available pickups:', error);
      }
    });
  }

  get displayedPickups(): any[] {
    const list = this.activeTab === 'my' ? this.myPickups : this.availablePickups;
    if (!this.filterStatus) {
      return list;
    }
    return list.filter(p => p.status === this.filterStatus);
  }

  setTab(tab: 'my' | 'available'): void {
    this.activeTab = tab;
    this.filterStatus = '';
  }

  claimPickup(id: number): void {
    this.successMessage = '';
    this.errorMessage = '';
    this.pickupService.claimPickup(id).subscribe({
      next: () => {
        this.successMessage = 'Pickup task claimed successfully! It is now in your active assignments.';
        this.setTab('my');
        this.loadAllPickups();
        setTimeout(() => { this.successMessage = ''; }, 4000);
      },
      error: (err: any) => {
        this.errorMessage = err?.error?.message || 'Failed to claim task.';
        setTimeout(() => { this.errorMessage = ''; }, 4000);
      }
    });
  }

  updateStatus(id: number, status: string): void {
    this.pickupService.updatePickupStatus(id, status).subscribe({
      next: () => {
        this.successMessage = `Task updated to ${status}.`;
        this.loadAllPickups();
        setTimeout(() => { this.successMessage = ''; }, 4000);
      },
      error: (error: any) => {
        console.error('Error updating pickup:', error);
        this.errorMessage = error?.error?.message || 'Failed to update pickup.';
        setTimeout(() => { this.errorMessage = ''; }, 4000);
      }
    });
  }
}
