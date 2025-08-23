import { Component, ChangeDetectionStrategy, signal, Input } from '@angular/core';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [NgIf]
})
export class NavbarComponent {
  isMobileMenuOpen = signal(false);
  toggleMobileMenu() {
    this.isMobileMenuOpen.update(open => !open);
  }
}