import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShopsCarousel } from './shops-carousel';

describe('ShopsCarousel', () => {
  let component: ShopsCarousel;
  let fixture: ComponentFixture<ShopsCarousel>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShopsCarousel]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ShopsCarousel);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
