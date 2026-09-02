import { CanActivateFn } from '@angular/router';

export const donorGuard: CanActivateFn = (route, state) => {
  return true;
};
