#include<stdio.h>
#include<string.h>
#include<stdlib.h>
#include <sys/types.h>
#include <unistd.h>

#define		SLEN	1024
#define		NI	20

int main() {
    srand((unsigned int)(getpid()));

    FILE *f = NULL;
    char fName[4][SLEN];
    int i;

    int nump[NI] = { 10, 50, 100, 500, 750, 1000, 1500, 2000, 2500, 3000,
	           3500, 4000, 5000, 5500, 6000, 7000, 7500, 8000, 9000,
		   10000 };
    int numr = 10000;
    int numpForVarr = 10; /* Perms for varr case */
    int maxdepth = 5;

    strcpy(fName[0], "varp_stanford.rbac.config");
    strcpy(fName[1], "varp_hybrid.rbac.config");
    strcpy(fName[2], "varp_core.rbac.config");
    strcpy(fName[3], "varr_core.rbac.config");

    for(i = 0; i < 4; i++) {
	printf("Creating %s...", fName[i]); fflush(stdout);
	f = fopen(fName[i], "w");
	fprintf(f, "#UA\n");
	fprintf(f, "U XR ");
	
	if(i < 3) {
	    /* NI roles to which U is assigned */
	    int j;
	    for(j = 0; j < NI; j++) {
		fprintf(f, "R0_%d ", nump[j]);
	    }
	    fprintf(f, "\n"); /* UA done */

	    int hybridNumRoles[NI]; /* Num roles in hierarchy for hybrid */

	    fprintf(f, "#PA\nXR XP1 XP2 XP3\n");

#if 0
	    if(i == 0) { /* Stanford */
		for(j = 0; j < NI; j++) {
		    fprintf(f, "R%d_%d ", maxdepth-1, nump[j]);

		    int k;
		    for(k = 0; k < nump[j]; k++) {
			fprintf(f, "P%d ", k+1);
		    }
		    fprintf(f, "\n");
		}

		/* PA Done */
		fprintf(f, "#RH\n");
		for(j = 0; j < NI; j++) {
		    int k;
		    for(k = 0; k < maxdepth-1; k++) {
			fprintf(f, "R%d_%d R%d_%d\n", k, nump[j], k+1, nump[j]);
		    }
		}
	    }
	    else
#endif
    	    for(j = 0; j < NI; j++) {
    		if(i == 0) {
    		    hybridNumRoles[j] = maxdepth-1;
    		}
    		else if(i == 1) {
    		    hybridNumRoles[j] = (rand())%maxdepth;
    		}
    		else {
    		    hybridNumRoles[j] = 0;
    		}

    		fprintf(f, "R%d_%d ", hybridNumRoles[j], nump[j]);

    		int k;
    		for(k = 0; k < nump[j]; k++) {
    		    fprintf(f, "P%d ", k+1);
    		}
    		fprintf(f, "\n");
    	    }

    	    /* PA Done */
    	    fprintf(f, "#RH\n");
    	    for(j = 0; j < NI; j++) {
    		int k;
    		for(k = 0; k < hybridNumRoles[j]; k++) {
    		    fprintf(f, "R%d_%d R%d_%d\n", k, nump[j], k+1, nump[j]);
    		}
    	    }
    	}
	else {
	    /* As many roles as numr in UA */
	    int j;
	    for(j = 0; j < numr; j++) {
		fprintf(f, "R0_%d ", j+1);
	    }
	    fprintf(f, "\n"); /* UA done */

	    fprintf(f, "#PA\nXR XP0 XP1 XP2\n");

	    for(j = 0; j < numr; j++) {
		fprintf(f, "R0_%d ", j+1);
		int k;
		for(k = 0; k < numpForVarr; k++) {
		    fprintf(f, "P%d ", k+1);
		}
		fprintf(f, "\n");
	    }

	    /* PA done */
	    fprintf(f, "#RH\n");
	}

	fclose(f);
	printf("done!\n"); fflush(stdout);
    }
}
