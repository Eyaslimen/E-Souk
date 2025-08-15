import { Component, ChangeDetectionStrategy, signal, Input } from '@angular/core';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NavbarComponent {
  isMobileMenuOpen = signal(false);
  toggleMobileMenu() {
    this.isMobileMenuOpen.update(open => !open);
  }
}