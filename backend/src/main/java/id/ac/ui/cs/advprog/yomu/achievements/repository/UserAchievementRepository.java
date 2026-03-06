@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {

    boolean existsByUserAndAchievement(User user, Achievement achievement);

}