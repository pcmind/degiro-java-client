package cat.indiketa.degiro.model.updates;

import com.google.common.base.Preconditions;

import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @param <T> object type
 */
public abstract class DUpdate<T> {
    /**
     * object identifier
     *
     * @return object identifier
     */
    public abstract String getId();

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

    public static <T> DUpdate<T> ofDelete(String id) {
        Preconditions.checkNotNull(id, "id");
        return new Delete<>(id);
    }

    public static <T> DUpdate<T> ofCreate(String id, Supplier<T> d) {
        Preconditions.checkNotNull(id, "id");
        return new Create<>(id, d);
    }

    public static <T> DUpdate<T> ofUpdate(String id, Consumer<T> applyUpdate) {
        Preconditions.checkNotNull(id, "id");
        return new Update<>(id, applyUpdate);
    }

    static class Create<T> extends DUpdate<T> {
        private final String id;
        private final Supplier<T> data;

        public Create(String id, Supplier<T> data) {
            this.id = id;
            this.data = data;
        }

        @Override
        public String getId() {
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

    static class Update<T> extends DUpdate<T> {
        private final String id;
        private final Consumer<T> update;

        public Update(String id, Consumer<T> update) {
            this.id = id;
            this.update = update;
        }

        @Override
        public String getId() {
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

    static class Delete<T> extends DUpdate<T> {
        private final String id;

        public Delete(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public Kind getType() {
            return Kind.DELETED;
        }
    }
}
