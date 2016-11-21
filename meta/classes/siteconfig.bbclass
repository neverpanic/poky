python siteconfig_do_siteconfig () {
	shared_state = sstate_state_fromvars(d)
	if shared_state['task'] != 'populate_sysroot':
		return
	if not os.path.isdir(os.path.join(d.getVar('FILE_DIRNAME', True), 'site_config')):
		bb.debug(1, "No site_config directory, skipping do_siteconfig")
		return

	# do_siteconfig_gencache needs to run against the files just installed by
	# this recipe; when using isolated sysroots, TOOLCHAIN_OPTIONS contains
	# a sysroot that does not point to this location and the cache is generated
	# incorrectly. Fix this by applying an override that adds the required
	# search paths.
	localdata = bb.data.createCopy(d)
	localdata.setVar('OVERRIDES', 'siteconfig:' + d.getVar('OVERRIDES', True))
	bb.data.update_data(localdata)
	bb.build.exec_func('do_siteconfig_gencache', localdata)

	sstate_clean(shared_state, d)
	sstate_install(shared_state, d)
}

EXTRASITECONFIG ?= ""
EXTRASITECONFIG_CFLAGS ?= "-isystem${SYSROOT_DESTDIR}${includedir}"
EXTRASITECONFIG_LDFLAGS ?= "-L${SYSROOT_DESTDIR}${base_libdir} -L${SYSROOT_DESTDIR}${libdir}"

CFLAGS_append_siteconfig ?= " ${EXTRASITECONFIG_CFLAGS}"
LDFLAGS_append_siteconfig ?= " ${EXTRASITECONFIG_LDFLAGS}"

siteconfig_do_siteconfig_gencache () {
	mkdir -p ${WORKDIR}/site_config_${MACHINE}
	gen-site-config ${FILE_DIRNAME}/site_config \
		>${WORKDIR}/site_config_${MACHINE}/configure.ac
	cd ${WORKDIR}/site_config_${MACHINE}
	autoconf
	rm -f ${BPN}_cache
	CONFIG_SITE="" ${EXTRASITECONFIG} ./configure ${CONFIGUREOPTS} --cache-file ${BPN}_cache
	sed -n -e "/ac_cv_c_bigendian/p" -e "/ac_cv_sizeof_/p" \
		-e "/ac_cv_type_/p" -e "/ac_cv_header_/p" -e "/ac_cv_func_/p" \
		< ${BPN}_cache > ${BPN}_config
	mkdir -p ${SYSROOT_DESTDIR}${datadir}/${TARGET_SYS}_config_site.d
	cp ${BPN}_config ${SYSROOT_DESTDIR}${datadir}/${TARGET_SYS}_config_site.d

}

do_populate_sysroot[sstate-interceptfuncs] += "do_siteconfig "

EXPORT_FUNCTIONS do_siteconfig do_siteconfig_gencache
