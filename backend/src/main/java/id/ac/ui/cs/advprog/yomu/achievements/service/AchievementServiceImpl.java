@Service
public class AchievementServiceImpl implements AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;

    public AchievementServiceImpl(AchievementRepository achievementRepository,
                                  UserAchievementRepository userAchievementRepository) {
        this.achievementRepository = achievementRepository;
        this.userAchievementRepository = userAchievementRepository;
    }

    @Override
    public void unlockFirstReadAchievement(User user) {

        Achievement achievement = achievementRepository
                .findByName("First Read")
                .orElseThrow();

        if (!userAchievementRepository.existsByUserAndAchievement(user, achievement)) {

            UserAchievement ua = new UserAchievement();
            ua.setUser(user);
            ua.setAchievement(achievement);
            ua.setAchievedAt(LocalDateTime.now());

            userAchievementRepository.save(ua);
        }
    }
}