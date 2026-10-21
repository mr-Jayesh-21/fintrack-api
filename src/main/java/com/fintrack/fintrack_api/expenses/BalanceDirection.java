package com.fintrack.fintrack_api.expenses;

/**
 * Direction of a net balance from the current user's perspective.
 */
public enum BalanceDirection {
	CALLER_OWES,
	THEY_OWE_CALLER,
	SETTLED
}
