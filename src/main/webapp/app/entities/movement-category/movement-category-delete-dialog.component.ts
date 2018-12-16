import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IMovementCategory } from 'app/shared/model/movement-category.model';
import { MovementCategoryService } from './movement-category.service';

@Component({
    selector: 'jhi-movement-category-delete-dialog',
    templateUrl: './movement-category-delete-dialog.component.html'
})
export class MovementCategoryDeleteDialogComponent {
    movementCategory: IMovementCategory;

    constructor(
        private movementCategoryService: MovementCategoryService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.movementCategoryService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'movementCategoryListModification',
                content: 'Deleted an movementCategory'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-movement-category-delete-popup',
    template: ''
})
export class MovementCategoryDeletePopupComponent implements OnInit, OnDestroy {
    private ngbModalRef: NgbModalRef;

    constructor(private activatedRoute: ActivatedRoute, private router: Router, private modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ movementCategory }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(MovementCategoryDeleteDialogComponent as Component, {
                    size: 'lg',
                    backdrop: 'static'
                });
                this.ngbModalRef.componentInstance.movementCategory = movementCategory;
                this.ngbModalRef.result.then(
                    result => {
                        this.router.navigate([{ outlets: { popup: null } }], { replaceUrl: true, queryParamsHandling: 'merge' });
                        this.ngbModalRef = null;
                    },
                    reason => {
                        this.router.navigate([{ outlets: { popup: null } }], { replaceUrl: true, queryParamsHandling: 'merge' });
                        this.ngbModalRef = null;
                    }
                );
            }, 0);
        });
    }

    ngOnDestroy() {
        this.ngbModalRef = null;
    }
}
