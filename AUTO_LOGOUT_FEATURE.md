# Auto-Logout Feature

## Overview
The auto-logout feature automatically logs out users after a specified period of inactivity to enhance security.

## How It Works

### User Activity Tracking
The system monitors the following user activities:
- Mouse movements
- Mouse clicks
- Keyboard input
- Scrolling
- Touch events (for mobile devices)

### Timer Behavior
1. **Initial State**: Timer starts when user logs in
2. **Activity Detection**: Any user activity resets the timer
3. **Inactivity**: If no activity is detected for the configured duration, user is automatically logged out
4. **Throttling**: Activity checks are throttled to once per second to optimize performance

### Configuration
Users can configure the auto-logout timer in **Settings → Security → Auto-Logout Timer**
- **Minimum**: 5 minutes
- **Maximum**: 120 minutes (2 hours)
- **Default**: 30 minutes

### Implementation Details

#### Frontend Components

**`useAutoLogout` Hook** (`src/hooks/useAutoLogout.ts`)
- Custom React hook that manages the inactivity timer
- Listens to user activity events
- Automatically logs out when timeout is reached
- Supports enabling/disabling the feature

**Layout Component** (`src/components/Layout.tsx`)
- Loads user preferences on mount
- Activates auto-logout for authenticated customers (not admins)
- Updates when preferences change

**Settings Page** (`src/pages/SettingsPage.tsx`)
- Provides UI for configuring the timeout duration
- Shows "Active" badge to indicate feature is working
- Saves preferences to backend

#### Backend Support

**Preferences API** (`module7Service.ts`)
- `getPreferences()`: Retrieves user preferences including autoLogoutMinutes
- `updatePreferences()`: Saves updated preferences

### User Types

- **Customers**: Auto-logout is **enabled** by default
- **Admins**: Auto-logout is **disabled** (they need constant access)

### Testing

To test the auto-logout feature:

1. Log in as a customer (e.g., `akshat_agrawal` / `admin123`)
2. Go to **Settings → Security**
3. Set the **Auto-Logout Timer** to a low value (e.g., 1-2 minutes for testing)
4. Click **Save Preferences**
5. Remain inactive (no mouse/keyboard activity) for the configured duration
6. You should be automatically logged out and redirected to the login page

### Console Logging

When auto-logout occurs, you'll see this message in the browser console:
```
Auto-logout: Session expired due to inactivity
```

### Security Benefits

1. **Prevents Unauthorized Access**: Automatically secures the session if user walks away
2. **Compliance**: Meets security standards requiring session timeouts
3. **Customizable**: Users can set their preferred timeout duration
4. **User-Friendly**: Transparent behavior with clear settings UI

### Future Enhancements

Potential improvements:
- Warning dialog before logout (e.g., "You will be logged out in 60 seconds")
- Session extension option
- Activity countdown display in header
- Different timeout settings for different sections
- Remember last active page and restore after re-login

## Technical Stack

- **React**: UI framework
- **TypeScript**: Type safety
- **Custom Hooks**: `useAutoLogout` for encapsulated logic
- **Event Listeners**: Browser events for activity detection
- **Local Storage**: Preference persistence
- **REST API**: Backend preference storage
