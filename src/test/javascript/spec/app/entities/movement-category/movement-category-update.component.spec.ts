/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { CoachappTestModule } from '../../../test.module';
import { MovementCategoryUpdateComponent } from 'app/entities/movement-category/movement-category-update.component';
import { MovementCategoryService } from 'app/entities/movement-category/movement-category.service';
import { MovementCategory } from 'app/shared/model/movement-category.model';

describe('Component Tests', () => {
    describe('MovementCategory Management Update Component', () => {
        let comp: MovementCategoryUpdateComponent;
        let fixture: ComponentFixture<MovementCategoryUpdateComponent>;
        let service: MovementCategoryService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [CoachappTestModule],
                declarations: [MovementCategoryUpdateComponent]
            })
                .overrideTemplate(MovementCategoryUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(MovementCategoryUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(MovementCategoryService);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity', fakeAsync(() => {
                // GIVEN
                const entity = new MovementCategory(123);
                spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.movementCategory = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.update).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));

            it('Should call create service on save for new entity', fakeAsync(() => {
                // GIVEN
                const entity = new MovementCategory();
                spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.movementCategory = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.create).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));
        });
    });
});
