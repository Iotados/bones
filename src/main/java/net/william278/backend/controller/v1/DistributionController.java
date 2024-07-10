package net.william278.backend.controller.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import net.william278.backend.configuration.AppConfiguration;
import net.william278.backend.database.model.Channel;
import net.william278.backend.database.model.Distribution;
import net.william278.backend.database.model.Project;
import net.william278.backend.database.model.Version;
import net.william278.backend.database.repository.ChannelRepository;
import net.william278.backend.database.repository.DistributionRepository;
import net.william278.backend.database.repository.ProjectRepository;
import net.william278.backend.database.repository.VersionRepository;
import net.william278.backend.exception.ChannelNotFound;
import net.william278.backend.exception.ProjectNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Schema(name = "Distributions")
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class DistributionController {

    private final AppConfiguration configuration;
    private final ProjectRepository projects;
    private final ChannelRepository channels;
    private final DistributionRepository distributions;
    private final VersionRepository versions;

    @Autowired
    public DistributionController(AppConfiguration configuration, ProjectRepository projects,
                                  ChannelRepository channels, DistributionRepository distributions,
                                  VersionRepository versions) {
        this.configuration = configuration;
        this.projects = projects;
        this.channels = channels;
        this.distributions = distributions;
        this.versions = versions;
    }

    @ApiResponse(
            responseCode = "200"
    )
    @GetMapping(
            value = "/v1/projects/{project}/distributions",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @CrossOrigin
    public Iterable<Distribution> getProjectDistributions(@PathVariable String project) {
        final Project found = projects.findById(project).orElseThrow(ProjectNotFound::new);
        return distributions.findDistributionsByProject(found);
    }

    @ApiResponse(
            responseCode = "200"
    )
    @GetMapping(
            value = "/v1/projects/{project}/channels/{channel}/distributions",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @CrossOrigin
    public Iterable<Distribution> getProjectDistributionsByChannel(@PathVariable String project, @PathVariable String channel) {
        final Project foundProject = projects.findById(project).orElseThrow(ProjectNotFound::new);
        final Channel foundChannel = channels.findChannelByName(channel).orElseThrow(ChannelNotFound::new);
        return versions.getAllByProjectAndChannel(foundProject, foundChannel).stream()
                .map(Version::getDistribution).distinct().toList();
    }

}
