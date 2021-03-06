package tools.redfox.bamboo.github.status.action;

import com.atlassian.bamboo.chains.Chain;
import com.atlassian.bamboo.chains.ChainExecution;
import com.atlassian.bamboo.chains.ChainResultsSummary;
import com.atlassian.bamboo.chains.StageExecution;
import com.atlassian.bamboo.chains.plugins.PostChainAction;
import com.atlassian.bamboo.plan.PlanManager;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.github.GHCommitState;
import tools.redfox.bamboo.github.status.build.config.GithubStatusBuildConfiguration;
import tools.redfox.bamboo.github.status.service.GithubServiceInterface;

public class PostBuildAction extends AbstractGitHubStatusAction implements PostChainAction {

    PostBuildAction(PlanManager planManager, GithubServiceInterface gitHubService) {
        super(planManager, gitHubService);
    }

    @Override
    public void execute(@NotNull Chain chain, @NotNull ChainResultsSummary chainResultsSummary, @NotNull ChainExecution chainExecution) throws InterruptedException, Exception {
        GithubStatusBuildConfiguration config = GithubStatusBuildConfiguration.from(chain.getBuildDefinition().getCustomConfiguration());

        chainExecution.getStages()
                .stream()
                .filter((StageExecution stageExecution) -> !config.getExcludedStages().contains(stageExecution.getName()))
                .forEach((StageExecution stageExecution) -> {
                    if (stageExecution.isCompleted() && stageExecution.getBuilds().size() > 0 && !stageExecution.getBuilds().get(0).isCompleted()) {
                        pushUpdate(stageExecution, GHCommitState.FAILURE, "Skipped");
                    }
                });
    }
}
