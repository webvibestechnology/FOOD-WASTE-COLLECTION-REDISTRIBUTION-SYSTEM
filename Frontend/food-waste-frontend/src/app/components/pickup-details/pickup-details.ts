import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Pickup } from '../../services/pickup';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-pickup-details',
  standalone: true,
<<<<<<< HEAD
  imports: [DatePipe],
=======
  imports: [CommonModule, DatePipe, FormsModule],
>>>>>>> 64475a0 (complete)
  templateUrl: './pickup-details.html',
  styleUrl: './pickup-details.css'
})
export class PickupDetailsComponent implements OnInit {

  pickup: any;
  pickupId!: number;
  notes: string = '';
  isUpdating: boolean = false;
  successMessage: string = '';
  errorMessage: string = '';

  constructor(
    private pickupService: Pickup,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.pickupId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadPickup();
  }

  loadPickup(): void {
    this.pickupService.getPickupById(this.pickupId).subscribe({
      next: (data: any) => {
        this.pickup = data;
      },
      error: (error: any) => {
        console.error('Error loading pickup:', error);
        this.errorMessage = 'Failed to load pickup details.';
      }
    });
  }

  updateStatus(status: string): void {
    this.isUpdating = true;
    this.successMessage = '';
    this.errorMessage = '';

    this.pickupService.updatePickupStatus(this.pickupId, status, this.notes).subscribe({
      next: (updated: any) => {
        this.pickup = updated;
        this.notes = '';
        this.isUpdating = false;
        this.successMessage = `Pickup status updated to ${status} successfully.`;
        setTimeout(() => { this.successMessage = ''; }, 4000);
      },
      error: (error: any) => {
        console.error('Error updating pickup:', error);
        this.isUpdating = false;
        this.errorMessage = error?.error?.message || 'Failed to update pickup status.';
      }
    });
  }

  getMapUrl(address: string | undefined): string {
    if (!address) return '#';
    return `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(address)}`;
  }

  goBack(): void {
    this.router.navigate(['/volunteer/pickups']);
  }
}
