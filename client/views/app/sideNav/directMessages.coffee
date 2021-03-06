Template.directMessages.helpers
	rooms: ->
		# be sure to also sort on '_id' field, since 'name' is no longer unique
		return ChatSubscription.find { t: { $in: ['d']}, open: true }, { sort: 't': 1, 'name': 1, '_id': 1 }
		
	isActive: ->
		return 'active' if ChatSubscription.findOne({ t: { $in: ['d']}, f: { $ne: true }, open: true, rid: Session.get('openedRoom') }, { fields: { _id: 1 } })?

Template.directMessages.events
	'click .add-room': (e, instance) ->
		SideNav.setFlex "directMessagesFlex"
		SideNav.openFlex()
