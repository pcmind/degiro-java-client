package cat.indiketa.degiro.model;

import com.google.common.base.Preconditions;

import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @param <T> object type
 * @param <I> object id  type
 */
public abstract class DUpdate<T, I> {
    /**
     * object identifier
     *
     * @return object identifier
     */
    public abstract I getId();

    public abstract Kind getType();

    /**
     * Only valid on kind {@link Kind#CREATED}
     *
     * @return new created object
     */
    public T getNew() {
        throw new UnsupportedOperationException("getNew not support on " + getType() + " operation");
    }

    /**
     * Only valid on kind {@link Kind#UPDATED}
     *
     * @param existing call this method with existing instance to apply updates to it.
     */
    public T update(T existing) {
        throw new UnsupportedOperationException("update not support on " + getType() + " operation");
    }

    public enum Kind {
        DELETED,
        CREATED,
        UPDATED
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", DUpdate.class.getSimpleName() + "[", "]")
                .add("id=" + getId()).add("type=" + getType())
                .toString();
    }

    public static <T, I> DUpdate<T, I> ofDelete(I id) {
        Preconditions.checkNotNull(id, "id");
        return new Delete<>(id);
    }

    public static <T, I> DUpdate<T, I> ofCreate(I id, Supplier<T> d) {
        Preconditions.checkNotNull(id, "id");
        return new Create<>(id, d);
    }

    public static <T, I> DUpdate<T, I> ofUpdate(I id, Consumer<T> applyUpdate) {
        Preconditions.checkNotNull(id, "id");
        return new Update<>(id, applyUpdate);
    }

    static class Create<T, I> extends DUpdate<T, I> {
        private final I id;
        private final Supplier<T> data;

        public Create(I id, Supplier<T> data) {
            this.id = id;
            this.data = data;
        }

        @Override
        public I getId() {
            return id;
        }

        @Override
        public T getNew() {
            return data.get();
        }

        @Override
        public Kind getType() {
            return Kind.CREATED;
        }
    }

    static class Update<T, I> extends DUpdate<T, I> {
        private final I id;
        private final Consumer<T> update;

        public Update(I id, Consumer<T> update) {
            this.id = id;
            this.update = update;
        }

        @Override
        public I getId() {
            return id;
        }

        @Override
        public T update(T original) {
            update.accept(original);
            return original;
        }

        @Override
        public Kind getType() {
            return Kind.UPDATED;
        }
    }

    static class Delete<T, I> extends DUpdate<T, I> {
        private final I id;

        public Delete(I id) {
            this.id = id;
        }

        @Override
        public I getId() {
            return id;
        }

        @Override
        public Kind getType() {
            return Kind.DELETED;
        }
    }
}
