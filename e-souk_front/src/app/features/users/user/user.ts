import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { AuthService } from '../../../services/AuthService';
import { UserProfile } from '../../../interfaces/UserProfile';
import { UserService } from '../../../services/UserService';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-user',
  imports: [CommonModule],
  templateUrl: './user.html',
  styleUrl: './user.css'
})
export class User implements OnInit {
  profile: UserProfile | null = null;
  isLoading: boolean = false;
constructor(
    private userService: UserService,
    private cdr: ChangeDetectorRef // Pour forcer la détection des changements

  ) 
  {
  }
ngOnInit(): void {
  this.getUserProfile(); 
}
getUserProfile() {
    console.log('📡 Fetching user profile...');
    this.isLoading = true;
    
    this.userService.getProfile().subscribe({
      next: (profile) => {
        console.log('✅ User profile fetched successfully:', profile);
        
        this.profile = profile;
        this.isLoading = false;
        
        // Force la détection des changements
        this.cdr.detectChanges();
        
        console.log('🔄 Component updated with profile:', this.profile);
      },
      error: (error) => {
        console.error('❌ Error fetching user profile:', error);
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }
}
