package tools.redfox.bamboo.github.status.action;

import com.atlassian.bamboo.chains.Chain;
import com.atlassian.bamboo.chains.ChainExecution;
import com.atlassian.bamboo.chains.StageExecution;
import com.atlassian.bamboo.chains.plugins.PreChainAction;
import com.atlassian.bamboo.plan.PlanManager;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.github.GHCommitState;
import tools.redfox.bamboo.github.status.build.config.GithubStatusBuildConfiguration;
import tools.redfox.bamboo.github.status.service.GithubServiceInterface;

public class PreBuildAction extends AbstractGitHubStatusAction implements PreChainAction {
    PreBuildAction(PlanManager planManager, GithubServiceInterface gitHubService) {
        super(planManager, gitHubService);
    }

    @Override
    public void execute(@NotNull Chain chain, @NotNull ChainExecution chainExecution) throws InterruptedException, Exception {
        GithubStatusBuildConfiguration config = GithubStatusBuildConfiguration.from(chain.getBuildDefinition().getCustomConfiguration());

        chainExecution.getStages()
                .stream()
                .filter((StageExecution stageExecution) -> !config.getExcludedStages().contains(stageExecution.getName()))
                .forEach((StageExecution stageExecution) -> {
            pushUpdate(stageExecution, GHCommitState.PENDING);
        });
    }
}
