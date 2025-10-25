import { useEffect, useRef, useCallback } from 'react';
import { useAuth } from '../contexts/AuthContext';

/**
 * Custom hook to handle automatic logout after a period of inactivity.
 * Tracks user activity (mouse, keyboard, touch) and logs out when the timeout is reached.
 * 
 * @param inactivityMinutes - Number of minutes of inactivity before auto-logout
 * @param enabled - Whether auto-logout is enabled
 */
export const useAutoLogout = (inactivityMinutes: number, enabled: boolean = true) => {
  const { logout, isAuthenticated } = useAuth();
  const timeoutRef = useRef<number | null>(null);
  const lastActivityRef = useRef<number>(Date.now());
  const inactivityMinutesRef = useRef(inactivityMinutes);
  const enabledRef = useRef(enabled);
  const isAuthenticatedRef = useRef(isAuthenticated);

  // Keep refs updated
  useEffect(() => {
    console.log('[Auto-Logout] Refs updated - inactivityMinutes:', inactivityMinutes);
    inactivityMinutesRef.current = inactivityMinutes;
    enabledRef.current = enabled;
    isAuthenticatedRef.current = isAuthenticated;
  }, [inactivityMinutes, enabled, isAuthenticated]);

  const resetTimer = useCallback(() => {
    if (!enabledRef.current || !isAuthenticatedRef.current) {
      console.log('[Auto-Logout] Timer not started - enabled:', enabledRef.current, 'isAuthenticated:', isAuthenticatedRef.current);
      return;
    }

    // Clear existing timeout
    if (timeoutRef.current) {
      window.clearTimeout(timeoutRef.current);
      console.log('[Auto-Logout] Cleared previous timer');
    }

    // Update last activity time
    const now = Date.now();
    lastActivityRef.current = now;

    // Set new timeout
    const timeoutMs = inactivityMinutesRef.current * 60 * 1000;
    const logoutTime = new Date(now + timeoutMs);
    
    console.log(`[Auto-Logout] Timer RESET at ${new Date(now).toLocaleTimeString()}`);
    console.log(`[Auto-Logout] Will logout at ${logoutTime.toLocaleTimeString()} (${inactivityMinutesRef.current} minutes from now)`);
    console.log(`[Auto-Logout] Timeout set to: ${timeoutMs}ms (${timeoutMs / 1000} seconds = ${timeoutMs / 60000} minutes)`);
    
    timeoutRef.current = window.setTimeout(() => {
      const actualTime = new Date().toLocaleTimeString();
      console.log(`🔒 Auto-logout: Session expired due to inactivity at ${actualTime}`);
      console.log('[Auto-Logout] Expected logout time was:', logoutTime.toLocaleTimeString());
      console.log('[Auto-Logout] Clearing auth state and redirecting');
      
      // Clear all auth data first
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      localStorage.removeItem('hasAccounts');
      
      // Then call logout to update React state
      logout();
    }, timeoutMs) as unknown as number;
  }, [logout]);

  const handleActivity = useCallback(() => {
    console.log('[Auto-Logout] Activity detected, resetting timer');
    resetTimer();
  }, [resetTimer]);

  // Setup event listeners (only once)
  useEffect(() => {
    console.log('[Auto-Logout] Setting up event listeners - enabled:', enabled, 'isAuthenticated:', isAuthenticated, 'inactivityMinutes:', inactivityMinutes);
    
    if (!enabled || !isAuthenticated) {
      // Clean up if disabled or not authenticated
      console.log('[Auto-Logout] Cleaning up - not enabled or not authenticated');
      if (timeoutRef.current) {
        window.clearTimeout(timeoutRef.current);
        timeoutRef.current = null;
      }
      return;
    }

    // Start the timer initially
    console.log('[Auto-Logout] Starting initial timer');
    resetTimer();

    // Activity events to track
    const events = [
      'mousedown',
      'mousemove',
      'keypress',
      'scroll',
      'touchstart',
      'click',
    ];

    // Throttle activity tracking to prevent excessive resets
    let throttleTimeout: number | null = null;
    const throttledHandleActivity = () => {
      if (!throttleTimeout) {
        throttleTimeout = window.setTimeout(() => {
          handleActivity();
          throttleTimeout = null;
        }, 1000) as unknown as number; // Throttle to once per second
      }
    };

    // Add event listeners
    console.log('[Auto-Logout] Adding event listeners for:', events);
    events.forEach((event) => {
      window.addEventListener(event, throttledHandleActivity);
    });

    // Cleanup function
    return () => {
      console.log('[Auto-Logout] Cleaning up event listeners');
      if (timeoutRef.current) {
        window.clearTimeout(timeoutRef.current);
      }
      if (throttleTimeout) {
        window.clearTimeout(throttleTimeout);
      }
      events.forEach((event) => {
        window.removeEventListener(event, throttledHandleActivity);
      });
    };
  }, [enabled, isAuthenticated, resetTimer, handleActivity]);

  // Separate effect to update timer when inactivityMinutes changes
  useEffect(() => {
    if (enabled && isAuthenticated) {
      console.log('[Auto-Logout] Inactivity minutes changed to:', inactivityMinutes);
      resetTimer();
    }
  }, [inactivityMinutes, enabled, isAuthenticated, resetTimer]);

  // Return the remaining time (in seconds) - useful for showing countdown
  const getRemainingTime = useCallback(() => {
    if (!enabledRef.current || !isAuthenticatedRef.current) return null;
    
    const elapsed = Date.now() - lastActivityRef.current;
    const timeoutMs = inactivityMinutesRef.current * 60 * 1000;
    const remaining = Math.max(0, timeoutMs - elapsed);
    
    return Math.floor(remaining / 1000);
  }, []);

  return { getRemainingTime };
};
