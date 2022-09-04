package company.vk.education.siriusapp.ui.screens.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import coil.compose.AsyncImage
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import company.vk.education.siriusapp.R
import company.vk.education.siriusapp.ui.library.trips.LocalTripItemMappers
import company.vk.education.siriusapp.ui.screens.Screens
import company.vk.education.siriusapp.ui.screens.main.bottomsheet.Loading
import company.vk.education.siriusapp.ui.library.trips.TripItem
import company.vk.education.siriusapp.ui.library.trips.TripCard
import company.vk.education.siriusapp.ui.screens.main.bottomsheet.LocalTaxiInfoMappers
import company.vk.education.siriusapp.ui.library.trips.TripItemButtonState
import company.vk.education.siriusapp.ui.theme.*
import company.vk.education.siriusapp.ui.utils.collectViewEffects

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.userScreen(
    navController: NavHostController,
    userScreenDeps: UserScreenDeps
) {
    bottomSheet(
        route = Screens.User.route,
        arguments = listOf(
            navArgument(Screens.User.KEY_USER_ID) {
                type = NavType.StringType
                nullable = false
            }
        )
    ) { backStackEntry ->
        val viewModel = hiltViewModel<UserViewModel>()
        val userId = backStackEntry.arguments?.getString(Screens.User.KEY_USER_ID)
            ?: error("User id is missing.")

        viewModel.collectViewEffects { viewEffect ->
            when (viewEffect) {
                is UserScreenViewEffect.Navigate -> navController.navigate(viewEffect.route)
                else -> {}
            }
        }

        CompositionLocalProvider(
            LocalUserScreenIntentConsumer provides viewModel,
            LocalTripItemMappers provides userScreenDeps.userScreenMappers,
            LocalTaxiInfoMappers provides userScreenDeps.userScreenMappers,
        ) {
            UserScreen(userId, viewModel)
        }
    }
}

@Composable
fun UserScreen(
    userId: String? = null,
    viewModel: UserViewModel,
) {
    viewModel.consume(UserScreenIntent.OnUserIdAcquired(userId))
    val userState by viewModel.viewState.collectAsState()
    UserScreenContent(state = userState)
}

@Composable
fun UserScreenContent(state: UserState) {
    if (state.isLoading) {
        Loading()
    } else {
        Box(
            Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing16dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                UserScreenHeader()

                Spacer(modifier = Modifier.height(Spacing16dp))

                UserInfo(state)

                Spacer(modifier = Modifier.height(Spacing16dp))

                if (state.currentTrip != null) {
                    UserCurrentTrip(state)
                }

                if (state.scheduledTrips.isNotEmpty()) {
                    UserScheduledTrips(state)
                    Spacer(modifier = Modifier.height(Spacing16dp))
                }

                if (state.previousTrips.isNotEmpty()) {
                    UserPreviousTrips(state)
                    Spacer(modifier = Modifier.height(Spacing16dp))
                }
            }
        }
    }
}

@Composable
private fun UserPreviousTrips(state: UserState) {
    val intentConsumer = LocalUserScreenIntentConsumer.current
    Text(
        text = stringResource(id = R.string.previous_trips),
        style = AppTypography.headline
    )
    Spacer(modifier = Modifier.height(Spacing8dp))
    state.previousTrips.forEach {
        TripItem(
            tripCard = TripCard(
                it,
                0,
                isCurrentTrip = false,
                TripItemButtonState.INVISIBLE
            ),
            { intentConsumer.consume(UserScreenIntent.ShowTripDetails(it)) },  // todo
            {} // todo
        )
    }
}

@Composable
private fun UserScheduledTrips(state: UserState) {
    val intentConsumer = LocalUserScreenIntentConsumer.current
    Text(
        text = stringResource(id = R.string.scheduled_trips),
        style = AppTypography.headline
    )
    Spacer(modifier = Modifier.height(Spacing8dp))
    state.scheduledTrips.forEach {
        TripItem(
            tripCard = TripCard(
                it,
                0,
                isCurrentTrip = false,
                TripItemButtonState.INVISIBLE
            ),
            { intentConsumer.consume(UserScreenIntent.ShowTripDetails(it)) }, // todo,
            {} // todo
        )
    }
}

@Composable
private fun UserCurrentTrip(
    state: UserState
) {
    require(state.currentTrip != null) // todo: already checked it, fix.
    val intentConsumer = LocalUserScreenIntentConsumer.current

    Text(
        text = stringResource(id = R.string.current_trip),
        style = AppTypography.headline
    )
    Spacer(modifier = Modifier.height(Spacing8dp))
    TripItem(
        tripCard = TripCard(
            trip = state.currentTrip,
            dist = 0,
            isCurrentTrip = true,
            tripItemButtonState = TripItemButtonState.INVISIBLE
        ),
        onShowTripClicked = { intentConsumer.consume(UserScreenIntent.ShowTripDetails(state.currentTrip)) },
        onJoinTripClicked = { /* error? */ } // todo
    )
}

@Composable
private fun UserScreenHeader() {
    TopAppBar(
        elevation = 0.dp,
        backgroundColor = Color.White,
        title = {
            Column {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = Spacing16dp),
                    contentAlignment = Alignment.Center
                ) {
                    Divider(
                        Modifier
                            .fillMaxWidth(0.25f)
                            .clip(RoundedCornerShape(Spacing4dp)),
                        color = Color.LightGray,
                        thickness = 3.dp
                    )
                }
                Spacer(modifier = Modifier.height(Spacing8dp))
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(id = R.string.user),
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
    )
}

@Composable
private fun UserInfo(state: UserState) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        require(state.user != null)
        Column {

            Text(text = state.user.name, style = AppTypography.headlineMedium)

            Spacer(modifier = Modifier.height(Spacing4dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_rating),
                    contentDescription = stringResource(R.string.rating),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(Spacing12dp))
                Text(
                    text = state.user.rating.toInt().toString() + "%",
                    style = AppTypography.subhead.copy(color = TextHint)
                )
            }

            Spacer(modifier = Modifier.height(Spacing4dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo_vk),
                    contentDescription = stringResource(R.string.rating),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(Spacing12dp))
                Text(
                    text = "id${state.user.id}",
                    style = AppTypography.subhead.copy(color = TextHint)
                )
            }
        }

        Box(contentAlignment = Alignment.TopEnd) {
            Spacer(modifier = Modifier.fillMaxWidth())
            AsyncImage(
                model = state.user.imageUrl,
                contentDescription = stringResource(id = R.string.profile_pic),
                placeholder = painterResource(
                    id = R.drawable.profile_avatar_placeholder
                ),
                error = painterResource(
                    id = R.drawable.profile_avatar_placeholder
                ),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )
        }
    }
}
