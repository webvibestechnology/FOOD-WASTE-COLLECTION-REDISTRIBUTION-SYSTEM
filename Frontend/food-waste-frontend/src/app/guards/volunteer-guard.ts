import { CanActivateFn } from '@angular/router';

export const volunteerGuard: CanActivateFn = (route, state) => {
  return true;
};
