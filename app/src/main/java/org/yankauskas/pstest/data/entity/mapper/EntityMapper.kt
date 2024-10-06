package org.yankauskas.pstest.data.entity.mapper

abstract class EntityMapper<Entity : Any, Model> {

    abstract fun transform(entity: Entity): Model

    open fun transform(collection: Collection<Entity>): ArrayList<Model> {
        val list = arrayListOf<Model>()
        var model: Model
        for (entity in collection) {
            model = transform(entity)
            if (model != null) {
                list.add(model)
            }
        }
        return list
    }
}