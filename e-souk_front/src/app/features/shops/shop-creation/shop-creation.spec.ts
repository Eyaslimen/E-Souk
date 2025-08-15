import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShopCreation } from './shop-creation';

describe('ShopCreation', () => {
  let component: ShopCreation;
  let fixture: ComponentFixture<ShopCreation>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShopCreation]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ShopCreation);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
