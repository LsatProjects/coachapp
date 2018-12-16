import { IMovementCategory } from 'app/shared/model//movement-category.model';

export interface IMovement {
    id?: number;
    name?: string;
    abbreviation?: string;
    note?: string;
    movementCategory?: IMovementCategory;
}

export class Movement implements IMovement {
    constructor(
        public id?: number,
        public name?: string,
        public abbreviation?: string,
        public note?: string,
        public movementCategory?: IMovementCategory
    ) {}
}
