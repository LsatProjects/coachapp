/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { CoachappTestModule } from '../../../test.module';
import { MovementCategoryComponent } from 'app/entities/movement-category/movement-category.component';
import { MovementCategoryService } from 'app/entities/movement-category/movement-category.service';
import { MovementCategory } from 'app/shared/model/movement-category.model';

describe('Component Tests', () => {
    describe('MovementCategory Management Component', () => {
        let comp: MovementCategoryComponent;
        let fixture: ComponentFixture<MovementCategoryComponent>;
        let service: MovementCategoryService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [CoachappTestModule],
                declarations: [MovementCategoryComponent],
                providers: []
            })
                .overrideTemplate(MovementCategoryComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(MovementCategoryComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(MovementCategoryService);
        });

        it('Should call load all on init', () => {
            // GIVEN
            const headers = new HttpHeaders().append('link', 'link;link');
            spyOn(service, 'query').and.returnValue(
                of(
                    new HttpResponse({
                        body: [new MovementCategory(123)],
                        headers
                    })
                )
            );

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.query).toHaveBeenCalled();
            expect(comp.movementCategories[0]).toEqual(jasmine.objectContaining({ id: 123 }));
        });
    });
});
