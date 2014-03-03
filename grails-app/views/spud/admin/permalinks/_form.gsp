<fieldset>
	<div class="form-group">
		<label for="permalink.urlName" class="control-label col-sm-3 required">Source Url</label>
		<div class="col-sm-7">
			<g:textField name="permalink.urlName" class="form-control" value="${permalink?.urlName}" size="25" />
		</div>
	</div>

	<div class="form-group">
		<label for="permalink.destinationUrl" class="control-label col-sm-3 required">Destination Url</label>
		<div class="col-sm-7">
			<g:textField name="permalink.destinationUrl" class="form-control" value="${permalink?.destinationUrl}" size="25" />
		</div>
	</div>

</fieldset>


