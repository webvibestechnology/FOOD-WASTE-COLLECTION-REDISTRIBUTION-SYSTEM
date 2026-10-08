import { Component, Input } from '@angular/core';
import { LoadingSpinner } from '../../shared/loading-spinner/loading-spinner';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  imports: [RouterLink, LoadingSpinner],
  templateUrl: './loading-spinner.html',
  styleUrl: './loading-spinner.css'
})
export class LoadingSpinner {
  @Input() isLoading: boolean = false;
}