public interface extends JpaRepository<Set, Integer> {
    public Set findByName(String name);
}