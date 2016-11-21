# This class enables isolated sysroots per-recipe. This approach ensures that
# a recipe will only ever see the dependencies it declared in its recipes and
# no other dependencies. This will avoid "automagic" dependencies that are
# detected in the build environment and used if they are available and it will
# ensure that recipes will undeclared dependencies will fail to build.

ISOLATED_SYSROOTS = "1"

# Directory where a per-recipe sysroot will be set up; this must contain ${PN}
# in some way, or you will be in trouble; A folder in ${WORKDIR} should be
# a good choice.
PRIVATE_STAGING_DIR = "${WORKDIR}/sysroots"

# We set Yocto's STAGING_DIR (which sets STAGING_DIR_NATIVE, STAGING_DIR_HOST
# and STAGING_DIR_TARGET) to the private staging directory of the current
# recipe, because quite a few recipes will likely use this variable to read
# from there. We use a different variable for writes into the staging area,
# because there are likely fewer locations that write to the staging area.
#
# Nobody except this class should write into STAGING_DIR; for writing into the
# staging area, use the WRITE_STAGING_*DIR_* variables.
STAGING_DIR = "${PRIVATE_STAGING_DIR}"


# Where to put files staged for the sysroot; for isolated-sysroots to be
# useful, this should contain ${PN}.
ISOLATED_STAGING_DIR = "${TMPDIR}/isolated-sysroots"
WRITE_STAGING_DIR    = "${ISOLATED_STAGING_DIR}/${PN}"

# The actual population of ${PRIVATE_STAGING_DIR} happens in
# prepare_isolated_sysroot in bitbake/bin/bitbake-worker for each task to be
# run.

## Adjustments for tools
# $FAKEROOTCMD is started before prepare_isolated_sysroot happens in
# bitbake-worker, so $FAKEROOTCMD must not reference recipe-local sysroots.
ISOLATED_PSEUDO_PREFIX = "${ISOLATED_STAGING_DIR}/pseudo-native/${BUILD_SYS}"
FAKEROOTBASEENV = "PSEUDO_PREFIX=${ISOLATED_PSEUDO_PREFIX}${prefix_native} PSEUDO_DISABLED=1"
FAKEROOTCMD = "${ISOLATED_PSEUDO_PREFIX}${bindir_native}/pseudo"
FAKEROOTENV = "PSEUDO_PREFIX=${ISOLATED_PSEUDO_PREFIX}${prefix_native} PSEUDO_LOCALSTATEDIR=${PSEUDO_LOCALSTATEDIR} PSEUDO_PASSWD=${PSEUDO_PASSWD} PSEUDO_NOSYMLINKEXP=1 PSEUDO_DISABLED=0"
