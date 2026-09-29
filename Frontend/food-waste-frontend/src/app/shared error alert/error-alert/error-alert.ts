import {
  Component,
  EventEmitter,
  Input,
  Output
} from '@angular/core';

@Component({
  selector: 'app-error-alert',
  standalone: true,
  imports: [],
  templateUrl: './error-alert.html',
  styleUrl: './error-alert.css'
})
export class ErrorAlert {

  @Input() message: string = '';

  @Output() dismiss = new EventEmitter<void>();

  close(): void {
    this.dismiss.emit();
  }
}