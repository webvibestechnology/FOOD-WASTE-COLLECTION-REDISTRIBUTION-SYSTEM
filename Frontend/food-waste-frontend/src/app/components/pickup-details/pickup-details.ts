import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Pickup } from '../../services/pickup';

@Component({
  selector: 'app-pickup-details',
  standalone: true,
  imports: [],
  templateUrl: './pickup-details.html',
  styleUrl: './pickup-details.css'
})
export class PickupDetails implements OnInit {

  pickup: any;
  pickupId!: number;

  constructor(
    private pickupService: Pickup,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.pickupId = Number(
      this.route.snapshot.paramMap.get('id')
    );

    this.loadPickup();
  }

  loadPickup(): void {
    this.pickupService.getPickupById(this.pickupId).subscribe({
      next: (data: any) => {
        this.pickup = data;
      },
      error: (error: any) => {
        console.error('Error loading pickup:', error);
      }
    });
  }

  updateStatus(status: string): void {
    this.pickupService
      .updatePickupStatus(this.pickupId, status)
      .subscribe({
        next: () => {
          this.loadPickup();
        },
        error: (error: any) => {
          console.error('Error updating pickup:', error);
        }
      });
  }

  goBack(): void {
    this.router.navigate(['/volunteer/pickup-requests']);
  }
}