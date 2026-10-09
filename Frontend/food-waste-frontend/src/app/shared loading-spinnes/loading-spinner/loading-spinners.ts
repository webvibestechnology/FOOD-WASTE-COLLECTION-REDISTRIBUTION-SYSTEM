import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './loading-spinners.html',
  styleUrl: './loading-spinners.css'
})
export class LoadingSpinnersComponent {
  @Input() isLoading: boolean = false;
}